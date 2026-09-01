package com.abdelkhalek.storehub.catalog.product.service;

import com.abdelkhalek.storehub.catalog.product.dto.CreateSaleEventDto;
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

    public CreateSaleEventDto create(UUID storeId, String name, String imageUrl ) {
        SaleEvent saleEvent = new SaleEvent();
        saleEvent.setName(name);
        saleEvent.setImageUrl(imageUrl);
        saleEvent.setStoreId(storeId);
        saleEvent.setSlug(generateUniqueSlug(storeId, name));
        SaleEvent saved = saleEventRepository.save(saleEvent);
        return new CreateSaleEventDto(
                saved.getId(),
                saved.getName(),
                saved.getImageUrl(),
                saved.getSlug(),
                saved.getDescription()
        );
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
