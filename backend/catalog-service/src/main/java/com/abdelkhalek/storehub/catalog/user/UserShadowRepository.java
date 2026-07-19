package com.abdelkhalek.storehub.catalog.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserShadowRepository extends JpaRepository<UserShadow, UUID> {
}