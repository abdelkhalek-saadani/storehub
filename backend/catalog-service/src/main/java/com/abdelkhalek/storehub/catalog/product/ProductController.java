package com.abdelkhalek.storehub.catalog.product;

import com.abdelkhalek.storehub.catalog.product.dto.ParentCategoryDTO;
import com.abdelkhalek.storehub.catalog.product.dto.ProductResponse;
import com.abdelkhalek.storehub.catalog.product.dto.SubCategoryDTO;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.entity.SaleEvent;
import com.abdelkhalek.storehub.catalog.product.repository.ProductRepository;
import com.abdelkhalek.storehub.catalog.product.repository.SubCategoryRepository;
import com.abdelkhalek.storehub.catalog.product.service.CategoryService;
import com.abdelkhalek.storehub.catalog.product.service.ProductService;
import com.abdelkhalek.storehub.catalog.product.service.SaleEventService;
import com.abdelkhalek.storehub.catalog.store.StoreService;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
