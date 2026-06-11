package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.ProductRequest;
import com.biliqis.hafsahs_place.dto.ProductUpdateRequest;
import com.biliqis.hafsahs_place.exception.ResourceNotFoundException;
import com.biliqis.hafsahs_place.model.Category;
import com.biliqis.hafsahs_place.model.Product;
import com.biliqis.hafsahs_place.repository.CategoryRepository;
import com.biliqis.hafsahs_place.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> getAvailableProducts(Pageable pageable) {
        return productRepository.findByIsAvailableTrue(pageable);
    }

    public Page<Product> getFeaturedProducts(Pageable pageable) {
        return productRepository.findByIsFeaturedTrue(pageable);
    }

    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findByIdWithImages(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    public Product getProductBySlug(String slug) {
        return productRepository.findBySlugWithImages(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
    }

    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable);
    }

    public List<Product> getLatestProducts() {
        return productRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Transactional
    public Product createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .category(category)
                .isCustomizable(request.getIsCustomizable())
                .isFeatured(request.getIsFeatured())
                .isAvailable(request.getIsAvailable())
                .slug(request.getSlug())
                .sku(request.getSku())
                .fabricType(request.getFabricType())
                .careInstructions(request.getCareInstructions())
                .build();

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductUpdateRequest request) {
        Product product = getProductById(id);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getBasePrice() != null) {
            product.setBasePrice(request.getBasePrice());
        }
        if (request.getIsCustomizable() != null) {
            product.setIsCustomizable(request.getIsCustomizable());
        }
        if (request.getIsFeatured() != null) {
            product.setIsFeatured(request.getIsFeatured());
        }
        if (request.getIsAvailable() != null) {
            product.setIsAvailable(request.getIsAvailable());
        }
        if (request.getSlug() != null) {
            product.setSlug(request.getSlug());
        }
        if (request.getSku() != null) {
            product.setSku(request.getSku());
        }
        if (request.getFabricType() != null) {
            product.setFabricType(request.getFabricType());
        }
        if (request.getCareInstructions() != null) {
            product.setCareInstructions(request.getCareInstructions());
        }

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}
