package com.abdelkhalek.storehub.catalog.product.service;

import com.abdelkhalek.storehub.catalog.inventory.entity.StockEntity;
import com.abdelkhalek.storehub.catalog.inventory.repository.StockRepository;
import com.abdelkhalek.storehub.catalog.product.ProductMapper;
import com.abdelkhalek.storehub.catalog.product.dto.CreateProductDto;
import com.abdelkhalek.storehub.catalog.product.dto.ProductResponse;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.entity.SubCategory;
import com.abdelkhalek.storehub.catalog.product.repository.ProductRepository;
import com.abdelkhalek.storehub.catalog.product.repository.SubCategoryRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final StockRepository stockRepository;
    private final SubCategoryRepository subCategoryRepository;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, StockRepository stockRepository, SubCategoryRepository subCategoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.stockRepository = stockRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    public List<ProductResponse> getProducts(UUID storeId, UUID saleEventId, Integer count) {
        List<ProductEntity> products = productRepository.findByStoreIdAndSaleEventId(storeId,
                saleEventId,
                PageRequest.of(0, count));
        return productMapper.toResponses(products);
    }

    public List<ProductResponse> getProducts(UUID storeId, Integer count) {
        List<ProductEntity> products = productRepository.findByStoreId(storeId, PageRequest.of(0, count));
        return productMapper.toResponses(products);
    }

    public List<ProductResponse> getBestSellerProducts(UUID storeSlug, Integer count) {
        List<ProductEntity> products = productRepository
                .findByStoreIdAndIsBestSellerIsTrue(storeSlug, PageRequest.of(0, count));
        return productMapper.toResponses(products);
    }

    @Transactional
    public CreateProductDto create(UUID storeId, String name, BigDecimal unitPrice,
                                   int initialQty, String imageUrl, boolean isBestSeller,
                                   UUID categoryId) {

        ProductEntity productEntity =
                ProductEntity.builder()
                        .storeId(storeId)
                        .name(name)
                        .unitPrice(unitPrice)
                        .imageUrl(imageUrl)
                        .isBestSeller(isBestSeller)
                        .build();
        if (categoryId != null) {
            SubCategory sc = subCategoryRepository.findById(categoryId).orElseThrow();
            productEntity.setSubCategory(sc);
        }

        productEntity = productRepository.save(productEntity);

        StockEntity stockEntity =
                StockEntity.builder()
                        .productId(productEntity.getId())
                        .storeId(storeId)
                        .quantityOnHand(initialQty)
                        .quantityAvailable(initialQty)
                        .quantityReserved(0)
                        .build();
        stockRepository.save(stockEntity);

        return new CreateProductDto(
                productEntity.getName(),
                productEntity.getUnitPrice(),
                stockEntity.getQuantityOnHand(),
                productEntity.getImageUrl(),
                productEntity.getIsBestSeller(),
                categoryId);
    }


}
