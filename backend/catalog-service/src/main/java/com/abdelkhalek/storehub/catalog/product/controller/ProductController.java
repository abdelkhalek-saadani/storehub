package com.abdelkhalek.storehub.catalog.product.controller;

import com.abdelkhalek.storehub.catalog.product.ProductMapper;
import com.abdelkhalek.storehub.catalog.product.dto.CreateProductDto;
import com.abdelkhalek.storehub.catalog.product.dto.ParentCategoryDTO;
import com.abdelkhalek.storehub.catalog.product.dto.ProductResponse;
import com.abdelkhalek.storehub.catalog.product.dto.SubCategoryDTO;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.entity.SaleEvent;
import com.abdelkhalek.storehub.catalog.product.repository.ProductRepository;
import com.abdelkhalek.storehub.catalog.product.service.CategoryService;
import com.abdelkhalek.storehub.catalog.product.service.ProductService;
import com.abdelkhalek.storehub.catalog.product.service.SaleEventService;
import com.abdelkhalek.storehub.catalog.store.service.StoreService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final SaleEventService saleEventService;
    private final StoreService storeService;

    @PostMapping("products")
    public ResponseEntity<CreateProductDto> create(@AuthenticationPrincipal Jwt jwt,
                                                   @RequestBody CreateProductDto request) {
        UUID storeId = storeService.getStoreId(jwt.getSubject());
        CreateProductDto created = productService.create(storeId, request.name(), request.unitPrice(),
                request.initialQty());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("products")
    public ResponseEntity<Page<ProductResponse>> findAll(
            @RequestParam UUID storeId,
            @RequestParam(required = false) String saleEvent,
            @RequestParam(required = false) Boolean isBestSeller,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<String> categories,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<ProductEntity> page = productRepository.findAll(
                ProductSpecifications.filter(storeId, minPrice, maxPrice, categories,
                        isBestSeller, saleEvent),
                pageable
        );

        Page<ProductResponse> response =
                page.map(productMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("products/explorer")
    public ResponseEntity<List<ProductResponse>> explorer(
            @RequestParam @NotNull UUID storeId,
            @RequestParam(required = false) UUID saleEventId,
            @RequestParam(required = false) Boolean isBestSeller,
            @RequestParam(required = false, defaultValue = "20") @Min(1) Integer count
    ) {
        if (isBestSeller) {
            return ResponseEntity.ok(productService.getBestSellerProducts(storeId, count));
        }
        List<ProductResponse> products = saleEventId != null ? productService.getProducts(storeId,
                saleEventId, count) : productService.getProducts(storeId, count);
        return ResponseEntity.ok(products);
    }

    @GetMapping("categories/subcategories")
    public ResponseEntity<List<SubCategoryDTO>> getSubCategories(@RequestParam UUID storeId,
                                                                 @RequestParam(required = false) @Min(1) Integer count) {


        List<SubCategoryDTO> subCategories = count != null ?
                categoryService.getSubCategories(storeId, count) : categoryService.getSubCategories(storeId);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("categories/parents")
    public ResponseEntity<List<ParentCategoryDTO>> getParentsWithSubs(@RequestParam UUID storeId) {
        return ResponseEntity.ok(categoryService.getParentCategories(storeId));
    }

    @GetMapping("sale-events")
    public ResponseEntity<List<SaleEvent>> getSaleEvents(@RequestParam UUID storeId,
                                                         @RequestParam(required = false,
                                                                 defaultValue = "6") @Min(1) Integer count) {
        return ResponseEntity.ok(saleEventService.getSaleEvents(storeId, count));
    }

}
