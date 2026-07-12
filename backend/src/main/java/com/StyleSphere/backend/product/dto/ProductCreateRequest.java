package com.StyleSphere.backend.product.dto;

import java.math.BigDecimal;

public class ProductCreateRequest {

    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;

    public ProductCreateRequest() {
    }

    public ProductCreateRequest(String categoryName, String name,
                                String description, BigDecimal price,
                                Integer stockQuantity) {
        this.categoryName = categoryName;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
