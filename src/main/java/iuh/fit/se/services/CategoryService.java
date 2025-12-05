package iuh.fit.se.services;

import iuh.fit.se.dtos.category.CategoryRequestDTO;
import iuh.fit.se.dtos.category.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {

    //Get all parent categories (parentCategoryId = NULL)
    List<CategoryResponseDTO> getParentCategories();

    //Get all child categories by parent category ID
    List<CategoryResponseDTO> getSubCategories(Long parentId);

    //Add category
    CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO);

    //update category
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequestDTO);

    //Delete category (parent or sub)
    void deleteCategory(Long id);
}
