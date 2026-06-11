package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.exception.ResourceNotFoundException;
import com.biliqis.hafsahs_place.model.Product;
import com.biliqis.hafsahs_place.model.ProductImage;
import com.biliqis.hafsahs_place.repository.ProductImageRepository;
import com.biliqis.hafsahs_place.repository.ProductRepository;
import com.biliqis.hafsahs_place.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products/{productId}/images")
public class ProductImageController {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public ResponseEntity<List<ProductImage>> getProductImages(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductImage> uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "altText", required = false) String altText,
            @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary) throws IOException {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Map uploadResult = cloudinaryService.uploadProductImage(file);
        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        int displayOrder = (int) productImageRepository.countByProductId(productId);

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .cloudinaryPublicId(publicId)
                .altText(altText != null ? altText : product.getName())
                .isPrimary(isPrimary)
                .displayOrder(displayOrder)
                .build();

        return new ResponseEntity<>(productImageRepository.save(image), HttpStatus.CREATED);
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) throws IOException {

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductImage", "id", imageId));

        if (image.getCloudinaryPublicId() != null) {
            cloudinaryService.deleteFile(image.getCloudinaryPublicId());
        }

        productImageRepository.delete(image);
        return ResponseEntity.noContent().build();
    }
}
