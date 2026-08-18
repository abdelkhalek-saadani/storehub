package com.abdelkhalek.storehub.payment.repository;

import com.abdelkhalek.storehub.payment.entity.PaymentEntity;
import com.abdelkhalek.storehub.payment.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID>, JpaSpecificationExecutor<PaymentEntity> {
    Optional<PaymentEntity> findByAuthorizationId(String authorizationId);
    Optional<PaymentEntity> findByCaptureId(String captureId);
    Optional<PaymentEntity> findByPaymentOrderId(String paymentOrderId);
    Optional<PaymentEntity> findByOrderId(UUID orderId);
    Optional<PaymentEntity> findByRefundId(String paymentId);
    Optional<PaymentEntity> findByCustomerId(UUID customerId);

}
