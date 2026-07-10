package com.abdelkhalek.storehub.catalog.product;

import com.abdelkhalek.storehub.catalog.product.dto.ParentCategoryDTO;
import com.abdelkhalek.storehub.catalog.product.dto.ProductResponse;
import com.abdelkhalek.storehub.catalog.product.dto.SubCategoryDTO;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.repository.ProductRepository;
import com.abdelkhalek.storehub.catalog.product.service.CategoryService;
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


    @GetMapping("products")
    public ResponseEntity<Page<ProductResponse>> findAll(
            @RequestParam UUID storeId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<String> categories,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<ProductEntity> page = productRepository.findAll(
                ProductSpecifications.filter(storeId, minPrice, maxPrice, categories),
                pageable
        );

        Page<ProductResponse> response =
                page.map(productMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("categories/subcategories")
    public ResponseEntity<List<SubCategoryDTO>> getSubCategories(@RequestParam UUID storeId) {
        return ResponseEntity.ok(categoryService.getSubCategories(storeId));
    }

    @GetMapping("categories/parents")
    public ResponseEntity<List<ParentCategoryDTO>> getParentsWithSubs(@RequestParam UUID storeId) {
        return ResponseEntity.ok(categoryService.getParentCategories(storeId));
    }

}
