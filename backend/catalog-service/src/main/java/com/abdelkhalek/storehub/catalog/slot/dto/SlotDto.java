package com.abdelkhalek.storehub.catalog.slot.dto;

import java.util.UUID;

public record SlotDto
        (
                UUID slotId,
                String slotLabel
        ){
}
