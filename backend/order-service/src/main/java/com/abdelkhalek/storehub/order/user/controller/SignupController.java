package com.abdelkhalek.storehub.order.user.controller;

import com.abdelkhalek.storehub.order.common.identity.KeycloakAdminService;
import com.abdelkhalek.storehub.order.user.UserEventPublisher;
import com.abdelkhalek.storehub.order.user.dto.SignupRequest;
import com.abdelkhalek.storehub.order.user.entity.User;
import com.abdelkhalek.storehub.order.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Tag(name = "Auth", description = "User registration")
public class SignupController {

    private final KeycloakAdminService keycloakAdminService;
    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;


    @Operation(summary = "Register a new user",
            description = "Creates the user in Keycloak and locally; " +
                    "rolls back the Keycloak user if local persistence fails.")
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "409", description = "Email already registered")
    @ApiResponse(responseCode = "500", description = "Signup failed, please retry")
    @PostMapping("/signup")
    public Mono<ResponseEntity<Map<String, String>>> signup(@Valid @RequestBody SignupRequest req) {
        return userRepository.existsByEmail(req.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(Map.of("message", "Email already registered")));
                    }
                    return createKeycloakAndLocalUser(req);
                });
    }

    private Mono<ResponseEntity<Map<String, String>>> createKeycloakAndLocalUser(SignupRequest req) {
        return keycloakAdminService.createUser(req)
                .flatMap(keycloakId -> saveLocalUser(req, keycloakId)
                        .flatMap(userEventPublisher::userCreated)
                        .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                                .body(Map.<String, String>of("message", "User created, please login")))
                        .onErrorResume(dbError -> {
                                    log.error(dbError.getMessage());
                                    return
                                            keycloakAdminService.deleteUser(keycloakId)
                                                    .then(Mono.error(new ResponseStatusException(
                                                            HttpStatus.INTERNAL_SERVER_ERROR,
                                                            "Signup failed, please retry")));
                                }
                        )
                );
    }

    private Mono<User> saveLocalUser(SignupRequest req, String keycloakId) {
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setAddress(req.getAddress());
        user.setPhoneNumber(req.getPhoneNumber());
        return userRepository.save(user);
    }
}