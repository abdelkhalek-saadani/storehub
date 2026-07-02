package com.abdelkhalek.storehub.order.store.employee;

import com.abdelkhalek.storehub.order.common.identity.KeycloakAdminService;
import com.abdelkhalek.storehub.order.user.User;
import com.abdelkhalek.storehub.order.user.UserRepository;
import com.abdelkhalek.storehub.order.store.MembershipRole;
import com.abdelkhalek.storehub.order.store.StoreMembership;
import com.abdelkhalek.storehub.order.store.StoreMembershipRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/stores/{storeId}/employees")
public class EmployeeController {

    private final UserRepository userRepository;
    private final StoreMembershipRepository membershipRepository;
    private final StoreAuthorizationService storeAuthorizationService;
    private final KeycloakAdminService keycloakAdminService;

    public EmployeeController(UserRepository userRepository,
                              StoreMembershipRepository membershipRepository,
                              StoreAuthorizationService storeAuthorizationService,
                              KeycloakAdminService keycloakAdminService) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.storeAuthorizationService = storeAuthorizationService;
        this.keycloakAdminService = keycloakAdminService;
    }

    @PostMapping
    public Mono<ResponseEntity<StoreMembership>> inviteEmployee(
            @PathVariable UUID storeId,
            @Valid @RequestBody InviteEmployeeRequest req,
            @AuthenticationPrincipal Jwt jwt) {

        String callerKeycloakId = jwt.getSubject();

        return userRepository.findByKeycloakId(callerKeycloakId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Caller (owner) not found")))
                .flatMap(caller -> storeAuthorizationService.requireOwnerOf(caller.getId(), storeId)
                        .then(resolveAndAssignEmployee(storeId, req.getEmail())));
    }

    private Mono<ResponseEntity<StoreMembership>> resolveAndAssignEmployee(UUID storeId, String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No account found for that email (the employee's email address)")))
                .flatMap(targetUser -> membershipRepository.existsByUserIdAndStoreId(targetUser.getId(), storeId)
                        .flatMap(alreadyMember -> {
                            if (alreadyMember) {
                                return Mono.error(new ResponseStatusException(
                                        HttpStatus.CONFLICT, "User is already a member of this store"));
                            }
                            return assignEmployee(targetUser, storeId);
                        })
                );
    }

    private Mono<ResponseEntity<StoreMembership>> assignEmployee(User targetUser, UUID storeId) {
        StoreMembership membership = new StoreMembership(targetUser.getId(), storeId, MembershipRole.EMPLOYEE);

        return membershipRepository.save(membership)
                .flatMap(savedMembership ->
                        keycloakAdminService.addRealmRole(targetUser.getKeycloakId(), MembershipRole.EMPLOYEE.name())
                                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(savedMembership))
                                .onErrorResume(err -> rollback(savedMembership, targetUser.getKeycloakId()))
                );
    }

    private Mono<ResponseEntity<StoreMembership>> rollback(StoreMembership savedMembership, String keycloakId) {
        return membershipRepository.delete(savedMembership)
                .then(membershipRepository.existsByUserIdAndRole(savedMembership.getUserId(), MembershipRole.EMPLOYEE))
                .flatMap(
                        (isEmployeeOfOtherStore) -> !isEmployeeOfOtherStore ?
                                keycloakAdminService.removeRealmRole(keycloakId, MembershipRole.EMPLOYEE.name())
                                : Mono.empty())
                .then(Mono.error(new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Employee assignment failed, please retry")));
    }
}
