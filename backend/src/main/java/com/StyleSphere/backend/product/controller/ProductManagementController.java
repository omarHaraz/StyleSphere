package com.StyleSphere.backend.product.controller;


import com.StyleSphere.backend.product.dto.ProductCreateRequest;
import com.StyleSphere.backend.product.dto.ProductResponse;
import com.StyleSphere.backend.product.dto.ProductUpdateRequest;
import com.StyleSphere.backend.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
public class ProductManagementController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable Long id) {

        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ProductResponse> createProduct(
            @ModelAttribute ProductCreateRequest request,
            @RequestParam(required = false) List<MultipartFile> images)
            throws IOException {

        ProductResponse response =
                productService.createProduct(request, images);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductUpdateRequest request,
            @RequestParam(required = false) List<MultipartFile> images)
            throws IOException {

        return ResponseEntity.ok(
                productService.updateProduct(id, request, images)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id)
            throws IOException {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}