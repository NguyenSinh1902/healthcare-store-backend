package iuh.fit.se.controllers;

import iuh.fit.se.dtos.product.ProductDetailDTO;
import iuh.fit.se.dtos.product.ProductRequestDTO;
import iuh.fit.se.dtos.product.ProductResponseDTO;
import iuh.fit.se.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/group/{group}")
    public ResponseEntity<Map<String, Object>> getProductsByGroup(@PathVariable String group) {
        List<ProductResponseDTO> products = productService.getProductsByGroup(group);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Products retrieved by group successfully");
        body.put("data", products);

        return ResponseEntity.ok(body);
    }


    //Get all products by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductResponseDTO> products = productService.getProductsByCategory(categoryId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Products retrieved by category successfully");
        body.put("data", products);

        return ResponseEntity.ok(body);
    }

    //Get product detail by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductDetail(@PathVariable Long id) {
        ProductDetailDTO detail = productService.getProductDetail(id);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Product detail retrieved successfully");
        body.put("data", detail);

        return ResponseEntity.ok(body);
    }

    //Filter products by brand, price, category, and sort
    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterProducts(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "featured") String sort
    ) {
        List<ProductResponseDTO> products = productService.filterProducts(brand, minPrice, maxPrice, categoryId, sort);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Filtered products retrieved successfully");
        body.put("filters", Map.of(
                "brand", brand,
                "minPrice", minPrice,
                "maxPrice", maxPrice,
                "categoryId", categoryId,
                "sort", sort
        ));
        body.put("data", products);

        return ResponseEntity.ok(body);
    }

    // Get all products
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "All products retrieved successfully");
        body.put("data", products);

        return ResponseEntity.ok(body);
    }

    //Create new product
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.createProduct(dto);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Product created successfully");
        body.put("data", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    //Update product
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO updated = productService.updateProduct(id, dto);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Product updated successfully");
        body.put("data", updated);

        return ResponseEntity.ok(body);
    }

    //Delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Category updated successfully (ID: " + id + ")");

        return ResponseEntity.ok(body);
    }
}
