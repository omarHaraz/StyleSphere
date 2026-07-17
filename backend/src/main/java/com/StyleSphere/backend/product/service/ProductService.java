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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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

    /**
     * EXTRACTS images from the already-loaded Entity collection.
     * ZERO extra database queries are fired here.
     */
    private List<String> extractPreloadedImageUrls(Product product) {
        if (product.getImages() == null) {
            return List.of();
        }
        return product.getImages()
                .stream()
                .map(ProductImage::getImageUrl)
                .toList();
    }

    @Cacheable(value = "products")
    public List<ProductResponse> getAllProducts() {
        System.out.println("=== Cache Miss! Fetching ALL products from Database ===");
        // Automatically leverages your EntityGraph optimized JOIN FETCH
        return productRepository.findAll()
                .stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.getCategory().getName(),
                        product.isEnabled(),
                        extractPreloadedImageUrls(product) // Pure Java collection mapping, NO extra queries!
                ))
                .toList();
    }

    @Cacheable(value = "product", key = "#id")
    public ProductResponse getProduct(Long id) {
        System.out.println("=== Cache Miss! Fetching single product ID: " + id + " from Database ===");
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found."));

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory().getName(),
                product.isEnabled(),
                extractPreloadedImageUrls(product)
        );
    }

    @CacheEvict(value = "products", allEntries = true)
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
        List<String> savedImageUrls = new ArrayList<>();

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
                savedImageUrls.add(upload.getImageUrl());
            }
        }

        return new ProductResponse(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getPrice(),
                savedProduct.getStockQuantity(),
                savedProduct.getCategory().getName(),
                savedProduct.isEnabled(),
                savedImageUrls
        );
    }

    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "product", key = "#id")
    })
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
        List<String> finalImageUrls = new ArrayList<>();

        if (images != null && !images.isEmpty()) {
            if (images.size() > 10) {
                throw new RuntimeException("Maximum 10 images are allowed.");
            }

            List<ProductImage> existingImages =
                    productImageRepository.findByProduct(product);

            for (ProductImage image : existingImages) {
                cloudinaryService.deleteImage(image.getPublicId());
            }

            productImageRepository.deleteAll(existingImages);

            List<UploadResponse> uploadedImages =
                    cloudinaryService.uploadImages(
                            images,
                            category.getName(),
                            updatedProduct.getName()
                    );

            int order = 1;
            for (UploadResponse upload : uploadedImages) {
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(upload.getImageUrl());
                productImage.setPublicId(upload.getPublicId());
                productImage.setDisplayOrder(order++);
                productImage.setProduct(updatedProduct);

                productImageRepository.save(productImage);
                finalImageUrls.add(upload.getImageUrl());
            }
        } else {
            finalImageUrls = extractPreloadedImageUrls(updatedProduct);
        }

        return new ProductResponse(
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getDescription(),
                updatedProduct.getPrice(),
                updatedProduct.getStockQuantity(),
                updatedProduct.getCategory().getName(),
                updatedProduct.isEnabled(),
                finalImageUrls
        );
    }

    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "product", key = "#id")
    })
    public void deleteProduct(Long id) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found."));

        List<ProductImage> images = productImageRepository.findByProduct(product);

        for (ProductImage image : images) {
            cloudinaryService.deleteImage(image.getPublicId());
        }

        productImageRepository.deleteAll(images);
        productRepository.delete(product);
    }
}