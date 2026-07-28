package com.abdelkhalek.storehub.payment.mapper;

import com.abdelkhalek.storehub.payment.dto.PaymentResponse;
import com.abdelkhalek.storehub.payment.entity.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper( componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "message", source = "message")
    @Mapping(target = "paymentId", source = "paymentEntity.id")
    PaymentResponse fromEntityToResponse(PaymentEntity paymentEntity, String message);

}
