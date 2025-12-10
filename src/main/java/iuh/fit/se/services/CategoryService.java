package iuh.fit.se.services;

import iuh.fit.se.dtos.category.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {

    //Get all parent categories (parentCategoryId = NULL)
    List<CategoryResponseDTO> getParentCategories();

    //Get all child categories by parent category ID
    List<CategoryResponseDTO> getSubCategories(Long parentId);

}
