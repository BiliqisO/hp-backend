package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.dto.ReviewRequest;
import com.biliqis.hafsahs_place.dto.ReviewResponse;
import com.biliqis.hafsahs_place.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            Authentication authentication,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse review = reviewService.createReview(authentication.getName(), request);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }

    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<Map<String, Object>> getReviewSummary(@PathVariable Long productId) {
        return ResponseEntity.ok(Map.of(
                "averageRating", reviewService.getAverageRating(productId) != null ? reviewService.getAverageRating(productId) : 0,
                "totalReviews", reviewService.getReviewCount(productId)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            Authentication authentication) {
        reviewService.deleteReview(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
