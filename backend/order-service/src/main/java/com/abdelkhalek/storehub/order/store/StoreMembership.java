package com.abdelkhalek.storehub.order.store;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("store_memberships")
@Data
@NoArgsConstructor
public class StoreMembership {

    @Id
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column("store_id")
    private UUID storeId;

    private MembershipRole role;



    public StoreMembership(UUID userId, UUID storeId, MembershipRole role) {
        this.userId = userId;
        this.storeId = storeId;
        this.role = role;
    }


}