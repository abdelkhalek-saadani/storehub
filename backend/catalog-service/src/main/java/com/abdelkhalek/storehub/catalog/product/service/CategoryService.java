package com.abdelkhalek.storehub.catalog.product.service;

import com.abdelkhalek.storehub.catalog.product.ProductMapper;
import com.abdelkhalek.storehub.catalog.product.dto.CreateCategoryDto;
import com.abdelkhalek.storehub.catalog.product.dto.ParentCategoryDTO;
import com.abdelkhalek.storehub.catalog.product.dto.SubCategoryDTO;
import com.abdelkhalek.storehub.catalog.product.entity.ParentCategory;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.entity.SubCategory;
import com.abdelkhalek.storehub.catalog.product.repository.ParentCategoryRepository;
import com.abdelkhalek.storehub.catalog.product.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ParentCategoryRepository parentCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductMapper productMapper;

    public SubCategoryDTO create(UUID storeId, String name, String imageUrl, UUID parentId) {
        SubCategory sc = new SubCategory();
        sc.setName(name);
        sc.setImageUrl(imageUrl);
        sc.setStoreId(storeId);
        ParentCategory parentCategory = parentCategoryRepository.findById(parentId).orElseThrow();
        sc.setParentCategory(parentCategory);
        SubCategory saved = subCategoryRepository.save(sc);
        return new SubCategoryDTO(saved.getId(), saved.getName(), imageUrl, parentCategory.getId());
    }

    public CreateCategoryDto createParent(UUID storeId, String name) {
        ParentCategory pc = new ParentCategory();
        pc.setName(name);
        pc.setStoreId(storeId);
        pc = parentCategoryRepository.save(pc);
        return new CreateCategoryDto(pc.getId(), name);
    }

    public List<SubCategoryDTO> getSubCategories(UUID storeId) {
        return productMapper.toSubCategoryDTOs(subCategoryRepository.findByStoreId(storeId));
    }
    public List<SubCategoryDTO> getSubCategories(UUID storeId, Integer count) {
        return productMapper.toSubCategoryDTOs(subCategoryRepository.findByStoreId(storeId,
                PageRequest.of(0, count)));
    }

    public List<ParentCategoryDTO> getParentCategories(UUID storeId) {
        return productMapper.toParentCategoryDTOs(parentCategoryRepository.findByStoreId(storeId));
    }
}
