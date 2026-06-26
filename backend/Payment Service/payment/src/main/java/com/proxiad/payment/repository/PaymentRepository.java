package com.proxiad.payment.repository;

import com.proxiad.payment.entity.PaymentEntity;
import com.proxiad.payment.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByAuthorizationId(String authorizationId);
    Optional<PaymentEntity> findByCaptureId(String captureId);
    Optional<PaymentEntity> findByPaymentOrderId(String paymentOrderId);
    Optional<PaymentEntity> findByOrderId(UUID orderId);
    Optional<PaymentEntity> findByRefundId(String paymentId);
    Optional<PaymentEntity> findByCustomerId(UUID customerId);

    @Query("SELECT p FROM PaymentEntity p WHERE " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:dateDebut IS NULL OR p.createdAt >= :dateDebut) AND " +
            "(:dateFin IS NULL OR p.createdAt <= :dateFin)")
    Page<PaymentEntity> findWithFilters(@Param("status") String status,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);

    List<PaymentEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);


    List<PaymentEntity> findByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, PaymentStatus status);
}
