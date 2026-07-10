package com.StyleSphere.backend.product.service;

import com.StyleSphere.backend.product.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();
    Product detById(Long id);
    Product saveProduct(Product product);
    void deleteProduct(Long id);
}
