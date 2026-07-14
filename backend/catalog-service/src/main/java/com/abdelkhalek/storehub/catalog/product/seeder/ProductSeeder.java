package com.abdelkhalek.storehub.catalog.product.seeder;

import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.entity.SubCategory;
import com.abdelkhalek.storehub.catalog.product.repository.ProductRepository;
import com.abdelkhalek.storehub.catalog.product.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

        create("d08d570a-22ee-41dd-a1c6-a6afea103fcd", "Laptop Pro 14", "High performance laptop", "1200.00", subCats.get("Laptops"));
        create("c2fddc73-92af-454a-8bdf-45fbf3eea60c", "Wireless Mouse", "Ergonomic mouse", "25.99", subCats.get("Accessories"));
        create("24818752-ae3b-4959-9fae-85917c459b7a", "Mechanical Keyboard", "RGB keyboard", "89.99", subCats.get("Accessories"));
        create("2ca93902-2e7c-4433-a29c-a08ac1e7ca62", "USB-C Charger", "65W fast charger", "19.99", subCats.get("Accessories"));
        create("33089686-7b9d-49e9-b344-7ec8312aec55", "Monitor 27\"", "4K display monitor", "299.99", subCats.get("Accessories"));
        create("89739022-6630-46ab-98da-a8bfe0495951", "External SSD 1TB", "Fast storage drive", "129.99", subCats.get("Accessories"));
        create("d2814c50-3848-4517-be66-d640d5ac88ab", "Office Chair", "Ergonomic chair", "199.99", subCats.get("Furniture"));
        create("89dd4386-66e3-4620-9743-11932d8e2bf2", "Webcam HD", "1080p webcam", "49.99", subCats.get("Accessories"));
        create("403b968c-8fb1-448f-8429-2989520eed64", "Noise Cancelling Headphones", "Over-ear headphones", "159.99", subCats.get("Accessories"));
        create("62acac9a-3615-431e-b089-35a8a25f8175", "Smartphone X", "Latest smartphone model", "999.99", subCats.get("Phones"));

        create(null, "Gaming Laptop 16", "High-end gaming laptop", "1899.99", subCats.get("Laptops"));
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
