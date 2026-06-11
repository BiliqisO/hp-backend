package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.WishlistItemResponse;
import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.exception.ResourceNotFoundException;
import com.biliqis.hafsahs_place.model.Product;
import com.biliqis.hafsahs_place.model.User;
import com.biliqis.hafsahs_place.model.WishlistItem;
import com.biliqis.hafsahs_place.repository.ProductRepository;
import com.biliqis.hafsahs_place.repository.UserRepository;
import com.biliqis.hafsahs_place.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(String userEmail) {
        User user = getUser(userEmail);
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(WishlistItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public WishlistItemResponse addToWishlist(String userEmail, Long productId) {
        User user = getUser(userEmail);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (wishlistRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new BadRequestException("Product is already in your wishlist");
        }

        WishlistItem item = wishlistRepository.save(WishlistItem.builder()
                .user(user)
                .product(product)
                .build());

        return WishlistItemResponse.from(item);
    }

    @Transactional
    public void removeFromWishlist(String userEmail, Long productId) {
        User user = getUser(userEmail);
        if (!wishlistRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new ResourceNotFoundException("Wishlist item", "productId", productId);
        }
        wishlistRepository.deleteByUserIdAndProductId(user.getId(), productId);
    }

    public boolean isInWishlist(String userEmail, Long productId) {
        User user = getUser(userEmail);
        return wishlistRepository.existsByUserIdAndProductId(user.getId(), productId);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
}
