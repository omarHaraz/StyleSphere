package com.StyleSphere.backend.product.repository;

import com.StyleSphere.backend.product.model.Category;
import com.StyleSphere.backend.product.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {


    @EntityGraph(attributePaths = {"category", "images"})
    List<Product> findAll();

    boolean existsByName(String name);
    List<Product> findByCategory(Category category);
}
