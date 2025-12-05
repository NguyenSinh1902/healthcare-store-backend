package iuh.fit.se.controllers;

import iuh.fit.se.dtos.category.CategoryRequestDTO;
import iuh.fit.se.dtos.category.CategoryResponseDTO;
import iuh.fit.se.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
// Chưa có @CrossOrigin (Frontend gọi sẽ bị lỗi CORS)
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Get all parent categories - Trả về List trực tiếp
    @GetMapping("/parents")
    public ResponseEntity<List<CategoryResponseDTO>> getParentCategories() {
        return ResponseEntity.ok(categoryService.getParentCategories());
    }

    // Get sub categories by parent ID - Trả về List trực tiếp
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<CategoryResponseDTO>> getSubCategories(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getSubCategories(parentId));
    }

    // Create new category
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        CategoryResponseDTO newCategory = categoryService.createCategory(categoryRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
    }

    // Delete category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // Update category by ID
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryRequestDTO));
    }
}