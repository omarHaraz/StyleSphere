package com.StyleSphere.backend.product.repository;

import com.StyleSphere.backend.product.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository
        extends JpaRepository<Category, Long>
{

    Optional<Category> findByName(String name);

    boolean existsByName(String name);




}
