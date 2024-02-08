package io.bytecode.ims.controller;

import io.bytecode.ims.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @RequestMapping(value = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadImageWithJson(@RequestPart("image") MultipartFile image,
                                      @RequestParam("name") String name) throws IOException {
        String imageUrl = categoryService.upload(image, name);
        return imageUrl;
    }

}
