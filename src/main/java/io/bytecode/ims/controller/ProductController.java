package io.bytecode.ims.controller;


import io.bytecode.ims.dto.ProductDto;
import io.bytecode.ims.model.Category;
import io.bytecode.ims.model.Product;
import io.bytecode.ims.service.ProductService;
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
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @RequestMapping(value = "/upload/{categoryId}",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImageWithJson(@RequestPart("image") MultipartFile image,
                                              @RequestParam("name") String name, @RequestParam("price") Integer price,
                                              @RequestParam("quantity") Integer quantity,
                                              @PathVariable Long categoryId) throws IOException {
        ProductDto productDto = new ProductDto(name, price, quantity);
        String imageUrl = productService.upload(image, productDto, categoryId);
        return new ResponseEntity<>("Product Added Successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/upload/{categoryId}/{productId}",
            method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProduct(@RequestPart("image") MultipartFile image,
                                                      @RequestParam("name") String name, @RequestParam("price") Integer price,
                                                      @RequestParam("quantity") Integer quantity,
                                                      @PathVariable Long categoryId,
                                                      @PathVariable Long productId ) throws IOException {
        ProductDto productDto = new ProductDto(name, price, quantity);
        String imageUrl = productService.updateProduct(image, productDto, categoryId, productId);
        return new ResponseEntity<>("Product Updated Successfully", HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/all/{categoryId}")
    public List<Product> getAllProductsByCategory(@PathVariable Long categoryId) {
        return productService.getAllProductsByCategory(categoryId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        productService.deleteById(id);
        return new ResponseEntity<>("Product has been deleted successfully", HttpStatus.OK);
    }


}
