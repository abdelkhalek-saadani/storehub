package com.abdelkhalek.storehub.order.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @GetMapping("me")
    public Mono<ResponseEntity<User>> getUser() {
        Mono<User> userMono = this.userService.getCurrentUser();
        return userMono.map(ResponseEntity::ok);
    }

    ;

    @GetMapping("last-store")
    public Mono<ResponseEntity<LastStoreResponse>> getLastStore() {
        return this.userService.getLastStore().map(ResponseEntity::ok);
    }
}
