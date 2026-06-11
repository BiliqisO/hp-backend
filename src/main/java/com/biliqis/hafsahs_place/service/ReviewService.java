package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.ReviewRequest;
import com.biliqis.hafsahs_place.dto.ReviewResponse;
import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.exception.ResourceNotFoundException;
import com.biliqis.hafsahs_place.model.Product;
import com.biliqis.hafsahs_place.model.Review;
import com.biliqis.hafsahs_place.model.User;
import com.biliqis.hafsahs_place.repository.ProductRepository;
import com.biliqis.hafsahs_place.repository.ReviewRepository;
import com.biliqis.hafsahs_place.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ReviewResponse createReview(String userEmail, ReviewRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        reviewRepository.findByProductIdAndUserId(product.getId(), user.getId())
                .ifPresent(existing -> {
                    throw new BadRequestException("You have already reviewed this product");
                });

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .isVerifiedPurchase(false)
                .build();

        return ReviewResponse.fromReview(reviewRepository.save(review));
    }

    public Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(ReviewResponse::fromReview);
    }

    public Double getAverageRating(Long productId) {
        return reviewRepository.getAverageRatingForProduct(productId);
    }

    public Long getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    @Transactional
    public void deleteReview(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }
}
