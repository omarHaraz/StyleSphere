package com.StyleSphere.backend.product.repository;

import com.StyleSphere.backend.product.model.Product;
import com.StyleSphere.backend.product.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository
        extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProduct(Product product);

    void deleteByProduct(Product product);

}
