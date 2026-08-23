package com.abdelkhalek.storehub.order.common.config;

import com.abdelkhalek.storehub.order.order.dto.AddressDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class AddressWritingConverter implements Converter<AddressDto, Json> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Json convert(AddressDto source) {
        try {
            return Json.of(mapper.writeValueAsString(source));
        } catch (Exception e) {
            throw new IllegalStateException("Could not serialize Address", e);
        }
    }
}