package com.abdelkhalek.storehub.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping()
public class CatalogController {

    @GetMapping()
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Salemu Alaykom");
    }


}
