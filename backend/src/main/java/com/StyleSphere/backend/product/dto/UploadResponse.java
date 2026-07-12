package com.StyleSphere.backend.product.dto;

public class UploadResponse {

    private String imageUrl;
    private String publicId;

    public UploadResponse() {
    }

    public UploadResponse(String imageUrl, String publicId) {
        this.imageUrl = imageUrl;
        this.publicId = publicId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
}