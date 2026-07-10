package com.abdelkhalek.storehub.catalog.product.seeder;

import com.abdelkhalek.storehub.catalog.product.entity.ParentCategory;
import com.abdelkhalek.storehub.catalog.product.entity.SubCategory;
import com.abdelkhalek.storehub.catalog.product.repository.ParentCategoryRepository;
import com.abdelkhalek.storehub.catalog.product.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Order(1)
public class CategorySeeder implements CommandLineRunner {

    private final ParentCategoryRepository parentCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    private static final UUID STORE_ID = UUID.fromString("7f76f5f6-0d95-4170-a719-365e9330fe64");

    @Override
    public void run(String... args) {
        if (parentCategoryRepository.count() > 0) return;

        ParentCategory electronics = createParent("Electronics");
        createSub("Phones", electronics);
        createSub("Laptops", electronics);
        createSub("Accessories", electronics);

        ParentCategory clothing = createParent("Clothing");
        createSub("Men", clothing);
        createSub("Women", clothing);
        createSub("Kids", clothing);

        ParentCategory home = createParent("Home & Kitchen");
        createSub("Furniture", home);
        createSub("Appliances", home);
    }

    private ParentCategory createParent(String name) {
        ParentCategory p = new ParentCategory();
        p.setName(name);
        p.setStoreId(STORE_ID);
        return parentCategoryRepository.save(p);
    }

    private void createSub(String name, ParentCategory parent) {
        SubCategory s = new SubCategory();
        s.setName(name);
        s.setStoreId(STORE_ID);
        s.setParentCategory(parent);
        subCategoryRepository.save(s);
    }
}