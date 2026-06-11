package com.biliqis.hafsahs_place.repository;

import com.biliqis.hafsahs_place.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(Long id);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.slug = :slug")
    Optional<Product> findBySlugWithImages(String slug);

    Optional<Product> findBySlug(String slug);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByIsAvailableTrue(Pageable pageable);

    Page<Product> findByIsFeaturedTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchProducts(String keyword, Pageable pageable);

    List<Product> findTop10ByOrderByCreatedAtDesc();

    long countByIsAvailableTrue();
}
