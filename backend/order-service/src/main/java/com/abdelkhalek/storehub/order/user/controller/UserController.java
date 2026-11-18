package com.abdelkhalek.storehub.order.user.controller;

import com.abdelkhalek.storehub.order.user.dto.LastStoreResponse;
import com.abdelkhalek.storehub.order.user.entity.User;
import com.abdelkhalek.storehub.order.user.mapper.UserMapper;
import com.abdelkhalek.storehub.order.user.repository.UserRepository;
import com.abdelkhalek.storehub.order.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "Authenticated user profile")
public class UserController {


    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get the current authenticated user")
    @GetMapping("me")
    public Mono<ResponseEntity<User>> getUser() {
        Mono<User> userMono = this.userService.getCurrentUser();
        return userMono.map(ResponseEntity::ok);
    }

    @PatchMapping
    public Mono<ResponseEntity<User>> updateUser(@Valid @RequestBody User user) {
        Mono<User> userMono = this.userService.getCurrentUser();
        return userMono.flatMap((user1) -> {
            User newUser = user1.toBuilder().build();
            userMapper.updateUser(user, newUser);
            return userRepository.save(newUser);
        }).map(ResponseEntity::ok);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get the last store visited by the current user")
    @GetMapping("last-store")
    public Mono<ResponseEntity<LastStoreResponse>> getLastStore() {
        return this.userService.getLastStore().map(ResponseEntity::ok);
    }
}
