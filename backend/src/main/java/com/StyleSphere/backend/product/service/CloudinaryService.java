package com.StyleSphere.backend.product.service;

import com.StyleSphere.backend.product.dto.UploadResponse;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public UploadResponse uploadImage(MultipartFile file,
                                      String categoryName,
                                      String productName) throws IOException {

        String folder = "products/"
                + categoryName + "/"
                + productName;

        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder
                )
        );

        return new UploadResponse(
                result.get("secure_url").toString(),
                result.get("public_id").toString()
        );
    }

    public void deleteImage(String publicId) throws IOException {

        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.emptyMap()
        );

    }

    public List<UploadResponse> uploadImages(
            List<MultipartFile> images,
            String categoryName,
            String productName) throws IOException {

        List<UploadResponse> uploadedImages = new ArrayList<>();

        for (MultipartFile image : images) {
            uploadedImages.add(
                    uploadImage(image, categoryName, productName)
            );
        }

        return uploadedImages;
    }

}