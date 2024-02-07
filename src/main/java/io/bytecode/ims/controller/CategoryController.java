package io.bytecode.ims.controller;

import io.bytecode.ims.model.Category;
import io.bytecode.ims.respository.CategoryRepository;
import io.bytecode.ims.service.S3Service;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final S3Service s3Service;
    private final CategoryRepository categoryRepository;

    @RequestMapping(value = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadImageWithJson(@RequestPart("image") MultipartFile image,
                                      @RequestParam("name") String name) throws IOException {
        File file = convertMultiPartFileToFile(image);
        String imageUrl = s3Service.uploadFile("ims-gallery", image.getOriginalFilename(), file);
        Category category = new Category();
        category.setName(name);
        category.setImageUrl(imageUrl);
        categoryRepository.save(category);
        return "Image uploaded successfully. Image URL: " + imageUrl;
    }

    private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

}
