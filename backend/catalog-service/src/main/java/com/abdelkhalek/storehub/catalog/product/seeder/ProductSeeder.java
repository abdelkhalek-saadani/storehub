package com.abdelkhalek.storehub.catalog.product.seeder;

import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.entity.SubCategory;
import com.abdelkhalek.storehub.catalog.product.repository.ProductRepository;
import com.abdelkhalek.storehub.catalog.product.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Profile("!test")
@Component
@Order(3)
@RequiredArgsConstructor
public class ProductSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final SubCategoryRepository subCategoryRepository;

    private static final UUID STORE_ID = UUID.fromString("7f76f5f6-0d95-4170-a719-365e9330fe64");

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) return;

        Map<String, SubCategory> subCats = subCategoryRepository.findByStoreId(STORE_ID).stream()
                .collect(Collectors.toMap(SubCategory::getName, s -> s));

        create(null, "Laptop Pro 14", "High performance laptop", "1200.00", subCats.get("Laptops"));
        create(null, "Wireless Mouse", "Ergonomic mouse", "25.99", subCats.get("Accessories"));
        create(null, "Mechanical Keyboard", "RGB keyboard", "89.99", subCats.get("Accessories"));
        create(null, "USB-C Charger", "65W fast charger", "19.99", subCats.get("Accessories"));
        create(null, "Monitor 27\"", "4K display monitor", "299.99", subCats.get("Accessories"));
        create(null, "External SSD 1TB", "Fast storage drive", "129.99", subCats.get("Accessories"));
        create(null, "Office Chair", "Ergonomic chair", "199.99", subCats.get("Furniture"));
        create(null, "Webcam HD", "1080p webcam", "49.99", subCats.get("Accessories"));
        create(null, "Noise Cancelling Headphones", "Over-ear headphones", "159.99", subCats.get("Accessories"));
        create(null, "Smartphone X", "Latest smartphone model", "999.99", subCats.get("Phones"));

        create(null, "Gaming Laptop 16", "High-end gaming laptop", "1899.99",
                subCats.get(
                "Laptops"));
        create(null, "Smartphone Y Lite", "Budget-friendly smartphone", "399.99", subCats.get("Phones"));
        create(null, "Men's Denim Jacket", "Classic blue denim jacket", "59.99", subCats.get("Men"));
        create(null, "Women's Summer Dress", "Lightweight floral dress", "45.99", subCats.get("Women"));
        create(null, "Kids Sneakers", "Comfortable running shoes for kids", "34.99", subCats.get("Kids"));
        create(null, "Wooden Dining Table", "6-seater dining table", "449.99", subCats.get("Furniture"));
        create(null, "Microwave Oven", "800W countertop microwave", "89.99", subCats.get("Appliances"));
        create(null, "Bluetooth Earbuds", "Wireless earbuds with charging case", "79.99", subCats.get("Accessories"));
        create(null, "Coffee Maker", "Drip coffee machine, 12-cup", "69.99", subCats.get("Appliances"));
        create(null, "Men's Running Shoes", "Breathable athletic shoes", "74.99", subCats.get("Men"));
    }

    private void create(String id, String name, String description, String price, SubCategory subCategory) {
        ProductEntity p = new ProductEntity();
        if (id != null) p.setId(UUID.fromString(id));
        p.setStoreId(STORE_ID);
        p.setName(name);
        p.setDescription(description);
        p.setUnitPrice(new BigDecimal(price));
        p.setSubCategory(subCategory);
        productRepository.save(p);
    }
}
