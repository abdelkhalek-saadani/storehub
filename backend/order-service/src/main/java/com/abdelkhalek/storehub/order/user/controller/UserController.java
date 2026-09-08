package com.abdelkhalek.storehub.order.user.controller;

import com.abdelkhalek.storehub.order.user.dto.LastStoreResponse;
import com.abdelkhalek.storehub.order.user.entity.User;
import com.abdelkhalek.storehub.order.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "Authenticated user profile")
public class UserController {


    private final UserService userService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get the current authenticated user")
    @GetMapping("me")
    public Mono<ResponseEntity<User>> getUser() {
        Mono<User> userMono = this.userService.getCurrentUser();
        return userMono.map(ResponseEntity::ok);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get the last store visited by the current user")
    @GetMapping("last-store")
    public Mono<ResponseEntity<LastStoreResponse>> getLastStore() {
        return this.userService.getLastStore().map(ResponseEntity::ok);
    }
}
