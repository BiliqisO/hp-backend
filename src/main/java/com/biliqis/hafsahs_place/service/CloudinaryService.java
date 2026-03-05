package com.biliqis.hafsahs_place.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public Map uploadFile(MultipartFile file, String folder) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "auto"
                ));
        return uploadResult;
    }

    public Map uploadProductImage(MultipartFile file) throws IOException {
        return uploadFile(file, "hafsahs-place/products");
    }

    public Map uploadCategoryImage(MultipartFile file) throws IOException {
        return uploadFile(file, "hafsahs-place/categories");
    }

    public Map uploadCustomOrderImage(MultipartFile file) throws IOException {
        return uploadFile(file, "hafsahs-place/custom-orders");
    }

    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
