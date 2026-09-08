package com.abdelkhalek.storehub.catalog.product.controller;

import com.abdelkhalek.storehub.catalog.product.ProductMapper;
import com.abdelkhalek.storehub.catalog.product.dto.*;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.entity.SaleEvent;
import com.abdelkhalek.storehub.catalog.product.repository.ProductRepository;
import com.abdelkhalek.storehub.catalog.product.service.CategoryService;
import com.abdelkhalek.storehub.catalog.product.service.ProductService;
import com.abdelkhalek.storehub.catalog.product.service.SaleEventService;
import com.abdelkhalek.storehub.catalog.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Products", description = "Products, categories, and sale events")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final SaleEventService saleEventService;
    private final StoreService storeService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new product")
    @ApiResponse(responseCode = "201", description = "Product created")
    @PostMapping("products")
    public ResponseEntity<CreateProductDto> create(@AuthenticationPrincipal Jwt jwt,
                                                   @RequestBody CreateProductDto request) {
        UUID storeId = storeService.getStoreId(jwt.getSubject());
        CreateProductDto created = productService.create(storeId, request.name(), request.unitPrice(),
                request.initialQty(), request.imageUrl(), request.isBestSeller());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "List products with filtering and pagination")
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

    @Operation(summary = "Get curated products for storefront display",
            description = "Returns best sellers or general products depending on the params " +
                    "provided.")
    @GetMapping("products/explorer")
    public ResponseEntity<List<ProductResponse>> explorer(
            @RequestParam @NotNull UUID storeId,
            @RequestParam(required = false) Boolean isBestSeller,
            @RequestParam(required = false, defaultValue = "20") @Min(1) Integer count
    ) {
        if (isBestSeller) {
            return ResponseEntity.ok(productService.getBestSellerProducts(storeId, count));
        }
        List<ProductResponse> products = productService.getProducts(storeId, count);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "List subcategories for a store")
    @GetMapping("categories/subcategories")
    public ResponseEntity<List<SubCategoryDTO>> getSubCategories(@RequestParam UUID storeId,
                                                                 @RequestParam(required = false) @Min(1) Integer count) {


        List<SubCategoryDTO> subCategories = count != null ?
                categoryService.getSubCategories(storeId, count) : categoryService.getSubCategories(storeId);
        return ResponseEntity.ok(subCategories);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new subcategory")
    @ApiResponse(responseCode = "201", description = "Subcategory created")
    @PostMapping("categories/subcategories")
    public ResponseEntity<SubCategoryDTO> createSubCategory(@AuthenticationPrincipal Jwt jwt,
                                                            @RequestBody SubCategoryDTO request) {
        UUID storeId = storeService.getStoreId(jwt.getSubject());
        SubCategoryDTO created = categoryService.create(storeId, request.name(),
                request.imageUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "List parent categories with their subcategories")
    @GetMapping("categories/parents")
    public ResponseEntity<List<ParentCategoryDTO>> getParentsWithSubs(@RequestParam UUID storeId) {
        return ResponseEntity.ok(categoryService.getParentCategories(storeId));
    }

    @Operation(summary = "List sale events for a store")
    @GetMapping("sale-events")
    public ResponseEntity<List<SaleEvent>> getSaleEvents(@RequestParam UUID storeId,
                                                         @RequestParam(required = false,
                                                                 defaultValue = "6") @Min(1) Integer count) {
        return ResponseEntity.ok(saleEventService.getSaleEvents(storeId, count));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new sale event")
    @ApiResponse(responseCode = "201", description = "Sale event created")
    @PostMapping("sale-events")
    public ResponseEntity<CreateSaleEventDto> createSaleEvent(@AuthenticationPrincipal Jwt jwt,
                                                              @RequestBody CreateSaleEventDto request) {
        UUID storeId = storeService.getStoreId(jwt.getSubject());
        CreateSaleEventDto created = saleEventService.create(storeId, request.name(), request.imageUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}
