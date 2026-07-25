package com.proxiad.payment.mapper;

import com.proxiad.payment.dto.PaymentResponse;
import com.proxiad.payment.entity.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper( componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "message", source = "message")
    @Mapping(target = "paymentId", source = "paymentEntity.id")
    PaymentResponse fromEntityToResponse(PaymentEntity paymentEntity, String message);

}
