package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.model.Testimonial;
import com.biliqis.hafsahs_place.repository.TestimonialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testimonials")
public class TestimonialController {

    @Autowired
    private TestimonialRepository testimonialRepository;

    @GetMapping
    public ResponseEntity<List<Testimonial>> getFeaturedTestimonials() {
        return ResponseEntity.ok(testimonialRepository.findByIsFeaturedTrueOrderByDisplayOrderAsc());
    }
}
