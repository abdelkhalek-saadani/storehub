package com.abdelkhalek.storehub.catalog.product.service;

import com.abdelkhalek.storehub.catalog.product.ProductMapper;
import com.abdelkhalek.storehub.catalog.product.dto.ParentCategoryDTO;
import com.abdelkhalek.storehub.catalog.product.dto.SubCategoryDTO;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.entity.SubCategory;
import com.abdelkhalek.storehub.catalog.product.repository.ParentCategoryRepository;
import com.abdelkhalek.storehub.catalog.product.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ParentCategoryRepository parentCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductMapper productMapper;

    public List<SubCategoryDTO> getSubCategories(UUID storeId) {
        return productMapper.toSubCategoryDTOs(subCategoryRepository.findByStoreId(storeId));
    }

    public List<ParentCategoryDTO> getParentCategories(UUID storeId) {
        return productMapper.toParentCategoryDTOs(parentCategoryRepository.findByStoreId(storeId));
    }
}
