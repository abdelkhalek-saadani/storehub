package com.abdelkhalek.storehub.catalog.inventory.enums;

public enum MovementType {
    NEW_BATCH,   // manager restocks
    DEDUCT,      // payment succeeded, physical stock will actually leave
    ADJUSTMENT   // manager correction (loss, damage, recount)
}
