package iuh.fit.se.controllers;

import iuh.fit.se.dtos.product.ProductDetailDTO;
import iuh.fit.se.dtos.product.ProductResponseDTO;
import iuh.fit.se.services.ProductService;
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

}
