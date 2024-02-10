package io.bytecode.ims.controller;

import io.bytecode.ims.model.Category;
import io.bytecode.ims.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @RequestMapping(value = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImageWithJson(@RequestPart("image") MultipartFile image,
                                              @RequestParam("name") String name) throws IOException {
        String imageUrl = categoryService.upload(image, name);
        return new ResponseEntity<>("Category has been added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
        return new ResponseEntity<>("Category has been deleted successfully", HttpStatus.OK);
    }

}
