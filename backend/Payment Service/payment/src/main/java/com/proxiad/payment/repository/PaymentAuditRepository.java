package com.proxiad.payment.repository;

import com.proxiad.payment.entity.PaymentAuditEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PaymentAuditRepository extends CrudRepository<PaymentAuditEntity, UUID> {
}
