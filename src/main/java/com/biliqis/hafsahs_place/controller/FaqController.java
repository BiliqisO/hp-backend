package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.model.Faq;
import com.biliqis.hafsahs_place.repository.FaqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
public class FaqController {

    @Autowired
    private FaqRepository faqRepository;

    @GetMapping
    public ResponseEntity<List<Faq>> getAllFaqs() {
        return ResponseEntity.ok(faqRepository.findByIsActiveTrueOrderByDisplayOrderAsc());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Faq>> getFaqsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(faqRepository.findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(category));
    }
}
