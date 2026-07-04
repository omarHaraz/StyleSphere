package com.StyleSphere.backend.service;

import com.StyleSphere.backend.model.Product;
import com.StyleSphere.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl  implements  ProductService{

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product detById(Long id) {
        return productRepository.findById(id).orElse(null);    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
