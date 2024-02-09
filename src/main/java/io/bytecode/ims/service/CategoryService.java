package io.bytecode.ims.service;

import io.bytecode.ims.model.Category;
import io.bytecode.ims.respository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final S3Service s3Service;

    private final AuthService authService;

    public String upload(MultipartFile imageFile, String name) throws  IOException {
        File file = convertMultiPartFileToFile(imageFile);
        String imageUrl = s3Service.uploadFile("ims-gallery", imageFile.getOriginalFilename(), file);
        Category category = new Category();
        category.setName(name);
        category.setImageUrl(imageUrl);
        category.setUser(authService.getCurrentUser());
        categoryRepository.save(category);
        return imageUrl;
    }


    private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }
}
