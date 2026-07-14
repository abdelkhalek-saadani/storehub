package com.abdelkhalek.storehub.catalog.product.service;

import com.abdelkhalek.storehub.catalog.product.entity.SaleEvent;
import com.abdelkhalek.storehub.catalog.product.repository.SaleEventRepository;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleEventService {

    private final SaleEventRepository repository;
    private final SaleEventRepository saleEventRepository;

    public SaleEvent create(SaleEvent event) {
        event.setSlug(generateUniqueSlug(event.getStoreId(), event.getName()));
        return repository.save(event);
    }

    private String generateUniqueSlug(UUID storeId,String name) {
        Slugify slugify = Slugify.builder().build();
        String base = slugify.slugify(name);

        String slug = base;
        int suffix = 2;
        while (repository.existsByStoreIdAndSlug(storeId, slug)) {
            slug = base + "-" + suffix++;
        }
        return slug;
    }


    public List<SaleEvent> getSaleEvents(UUID storeId, Integer count) {
        return saleEventRepository.findByStoreId(storeId, PageRequest.of(0, count));
    }
}
