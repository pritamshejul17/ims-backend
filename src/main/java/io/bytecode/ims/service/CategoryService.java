package io.bytecode.ims.service;

import io.bytecode.ims.exception.SpringImsException;
import io.bytecode.ims.model.Category;
import io.bytecode.ims.respository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void deleteById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        categoryRepository.delete(category.orElseThrow(() -> new SpringImsException("Category Not found")));
    }



    private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }
}
