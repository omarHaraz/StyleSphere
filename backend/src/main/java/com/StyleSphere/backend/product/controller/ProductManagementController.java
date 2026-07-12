package com.StyleSphere.backend.product.controller;


import com.StyleSphere.backend.product.dto.ProductCreateRequest;
import com.StyleSphere.backend.product.dto.ProductResponse;
import com.StyleSphere.backend.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
public class ProductManagementController
{
    @Autowired
    private ProductService productService;

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ProductResponse createProduct(
            @ModelAttribute ProductCreateRequest request,
            @RequestParam("images") List<MultipartFile> images
    ) throws IOException {
        System.out.println("===== CREATE PRODUCT CONTROLLER REACHED =====");

        return productService.createProduct(request, images);
    }


}
