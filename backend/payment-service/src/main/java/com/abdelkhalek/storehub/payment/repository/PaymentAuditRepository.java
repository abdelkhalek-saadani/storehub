package com.abdelkhalek.storehub.payment.repository;

import com.abdelkhalek.storehub.payment.entity.PaymentAuditEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PaymentAuditRepository extends CrudRepository<PaymentAuditEntity, UUID> {
}
