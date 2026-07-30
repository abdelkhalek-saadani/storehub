package com.abdelkhalek.storehub.catalog.product.service;

import com.abdelkhalek.storehub.catalog.product.ProductMapper;
import com.abdelkhalek.storehub.catalog.product.dto.ProductResponse;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.repository.ProductRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
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


}
