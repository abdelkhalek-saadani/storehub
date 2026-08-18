package com.abdelkhalek.storehub.payment.repository;

import com.abdelkhalek.storehub.payment.entity.PaymentEntity;
import com.abdelkhalek.storehub.payment.enums.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentSpecifications {

    public static Specification<PaymentEntity> captureIdEquals(String captureId) {
        return (root, query, cb) -> StringUtils.hasText(captureId)
                ? cb.equal(root.get("captureId"), captureId) : null;
    }

    public static Specification<PaymentEntity> authorizationIdEquals(String authorizationId) {
        return (root, query, cb) -> StringUtils.hasText(authorizationId)
                ? cb.equal(root.get("authorizationId"), authorizationId) : null;
    }

    public static Specification<PaymentEntity> customerIdEquals(UUID customerId) {
        return (root, query, cb) -> customerId != null
                ? cb.equal(root.get("customerId"), customerId) : null;
    }

    public static Specification<PaymentEntity> statusEquals(PaymentStatus status) {
        return (root, query, cb) -> status != null
                ? cb.equal(root.get("status"), status.name()) : null;
    }

    public static Specification<PaymentEntity> createdBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start != null && end != null) return cb.between(root.get("createdAt"), start, end);
            if (start != null) return cb.greaterThanOrEqualTo(root.get("createdAt"), start);
            if (end != null) return cb.lessThanOrEqualTo(root.get("createdAt"), end);
            return null;
        };
    }
}
