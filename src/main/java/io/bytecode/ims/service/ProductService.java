package io.bytecode.ims.service;

import io.bytecode.ims.dto.ProductDto;
import io.bytecode.ims.exception.SpringImsException;
import io.bytecode.ims.model.Category;
import io.bytecode.ims.model.Product;
import io.bytecode.ims.respository.CategoryRepository;
import io.bytecode.ims.respository.ProductRepository;
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
public class ProductService {

    private final S3Service s3Service;
    private final AuthService authService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public String upload(MultipartFile imageFile, ProductDto productDto, Long categoryId) throws  IOException {
        File file = convertMultiPartFileToFile(imageFile);
        String imageUrl = s3Service.uploadFile("ims-gallery", imageFile.getOriginalFilename(), file);
        Product product = new Product();
        Optional<Category> category = categoryRepository.findById(categoryId);
        product.setCategory(category.orElseThrow(() -> new SpringImsException("Category Not found ")));
        product.setUser(authService.getCurrentUser());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setName(productDto.getName());
        product.setImageUrl(imageUrl);
        productRepository.save(product);
        return imageUrl;
    }

    private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        productRepository.delete(product.orElseThrow(() -> new SpringImsException("Product Not found")));
    }

    public List<Product> getAllProductsByCategory(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);

        return productRepository.findAllByCategory(category.orElseThrow(() -> new SpringImsException("Category Not found")));
    }
}
