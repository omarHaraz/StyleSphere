package com.StyleSphere.backend.product.service;

import com.StyleSphere.backend.exception.DuplicateResourceException;
import com.StyleSphere.backend.exception.ResourceNotFoundException;
import com.StyleSphere.backend.product.dto.ProductCreateRequest;
import com.StyleSphere.backend.product.dto.ProductResponse;
import com.StyleSphere.backend.product.dto.ProductUpdateRequest;
import com.StyleSphere.backend.product.dto.UploadResponse;
import com.StyleSphere.backend.product.model.Category;
import com.StyleSphere.backend.product.model.Product;
import com.StyleSphere.backend.product.model.ProductImage;
import com.StyleSphere.backend.product.repository.CategoryRepository;
import com.StyleSphere.backend.product.repository.ProductImageRepository;
import com.StyleSphere.backend.product.repository.ProductRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {



    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ProductImageRepository productImageRepository;


    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.getCategory().getName(),
                        product.isEnabled()
                ))
                .toList();
    }

    public ProductResponse getProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found."));

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory().getName(),
                product.isEnabled()
        );
    }

    public ProductResponse createProduct(ProductCreateRequest request,
                                         List<MultipartFile> images) throws IOException {

        if (productRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Product name already exists.");
        }

        Category category = categoryRepository
                .findByName(request.getCategoryName())
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(request.getCategoryName());
                    return categoryRepository.save(newCategory);
                });

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setEnabled(true);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        if (images != null && !images.isEmpty()) {

            if (images.size() > 10) {
                throw new BadRequestException("Maximum 10 images are allowed.");
            }

            List<UploadResponse> uploadedImages =
                    cloudinaryService.uploadImages(
                            images,
                            category.getName(),
                            savedProduct.getName()
                    );

            int order = 1;

            for (UploadResponse upload : uploadedImages) {

                ProductImage productImage = new ProductImage();

                productImage.setImageUrl(upload.getImageUrl());
                productImage.setPublicId(upload.getPublicId());
                productImage.setDisplayOrder(order++);
                productImage.setProduct(savedProduct);

                productImageRepository.save(productImage);
            }
        }

        return new ProductResponse(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getPrice(),
                savedProduct.getStockQuantity(),
                savedProduct.getCategory().getName(),
                savedProduct.isEnabled()
        );
    }

    public ProductResponse updateProduct(Long id,
                                         ProductUpdateRequest request,
                                         List<MultipartFile> images) throws IOException {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found."));

        if (!product.getName().equals(request.getName())
                && productRepository.existsByName(request.getName())) {

            throw new RuntimeException("Product name already exists.");
        }

        Category category = categoryRepository
                .findByName(request.getCategoryName())
                .orElseGet(() -> {

                    Category newCategory = new Category();
                    newCategory.setName(request.getCategoryName());

                    return categoryRepository.save(newCategory);
                });

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        if (images != null && !images.isEmpty()) {

            if (images.size() > 10) {
                throw new RuntimeException("Maximum 10 images are allowed.");
            }

            // Delete old Cloudinary images
            List<ProductImage> existingImages =
                    productImageRepository.findByProduct(product);

            for (ProductImage image : existingImages) {
                cloudinaryService.deleteImage(image.getPublicId());
            }

            // Delete image records
            productImageRepository.deleteAll(existingImages);

            // Upload new images
            List<UploadResponse> uploadedImages =
                    cloudinaryService.uploadImages(
                            images,
                            category.getName(),
                            updatedProduct.getName()
                    );

            int order = 1; // 1. You initialize the counter here

            for (UploadResponse upload : uploadedImages) {

                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(upload.getImageUrl());
                productImage.setPublicId(upload.getPublicId());

                productImage.setDisplayOrder(order++);

                productImage.setProduct(updatedProduct);

                productImageRepository.save(productImage);
            }
        }

        return new ProductResponse(
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getDescription(),
                updatedProduct.getPrice(),
                updatedProduct.getStockQuantity(),
                updatedProduct.getCategory().getName(),
                updatedProduct.isEnabled()
        );
    }

    public void deleteProduct(Long id) throws IOException {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found."));

        List<ProductImage> images =
                productImageRepository.findByProduct(product);

        // Delete from Cloudinary
        for (ProductImage image : images) {
            cloudinaryService.deleteImage(image.getPublicId());
        }

        // Delete image records
        productImageRepository.deleteAll(images);

        // Soft delete product
        productRepository.delete(product);

    }



}
