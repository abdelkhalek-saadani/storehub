package com.abdelkhalek.storehub.catalog.slot.controller;


import com.abdelkhalek.storehub.catalog.slot.entity.SlotConfig;
import com.abdelkhalek.storehub.catalog.slot.service.SlotConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/slot-configs")
@RequiredArgsConstructor
public class SlotConfigController {

    private final SlotConfigService slotConfigService;

    @PostMapping
    public SlotConfig create(
            @AuthenticationPrincipal AuthenticatedPrincipal authenticatedPrincipal,
            @Valid @RequestBody SlotConfig config) {
        config.setTenantId(tenantId);
        return slotConfigService.create(config);
    }

    // Updates the rule and syncs future, unbooked, non-overridden slots automatically.
    @PutMapping("/{configId}")
    public SlotConfig update(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @PathVariable Long configId,
            @Valid @RequestBody SlotConfig config) {
        return slotConfigService.update(configId, tenantId, config);
    }
}
