package com.abdelkhalek.storehub.order.common.config;

import com.abdelkhalek.storehub.order.order.dto.AddressDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class AddressReadingConverter implements Converter<Json, AddressDto> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public AddressDto convert(Json source) {
        try {
            return mapper.readValue(source.asString(), AddressDto.class);
        } catch (Exception e) {
            throw new IllegalStateException("Could not deserialize Address", e);
        }
    }
}
