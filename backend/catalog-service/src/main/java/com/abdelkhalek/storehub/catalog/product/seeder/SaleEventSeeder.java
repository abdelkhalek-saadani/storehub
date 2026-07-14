package com.abdelkhalek.storehub.catalog.product.seeder;


import com.abdelkhalek.storehub.catalog.product.entity.SaleEvent;
import com.abdelkhalek.storehub.catalog.product.repository.SaleEventRepository;
import com.abdelkhalek.storehub.catalog.product.service.SaleEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


import java.util.UUID;

@Component
@Order(2)
@RequiredArgsConstructor
public class SaleEventSeeder implements CommandLineRunner {

    private final SaleEventRepository saleEventRepository;

    private static final UUID STORE_ID = UUID.fromString("7f76f5f6-0d95-4170-a719-365e9330fe64");
    private final SaleEventService saleEventService;

    @Override
    public void run(String... args) {
        if (saleEventRepository.count() > 0) return;

        create("Mother's Day", "/offers/mothers-day.jpg");
        create("Summer Sale", "/offers/summer.jpg");
        create("Father's Day", "/offers/fathers-day.jpg");
        create("Back To School", "/offers/back-to-school.jpg");
        create("New Arrivals", "/offers/new-arrivals.jpg");
        create("Mother's Day", "/offers/mothers-day.jpg");

    }

    private void create(String name, String imageUrl) {
       SaleEvent se = new SaleEvent();
       se.setName(name);
       se.setDescription("This is a special event");
       se.setImageUrl(imageUrl);
       se.setStoreId(STORE_ID);
       saleEventService.create(se);
    }

}

