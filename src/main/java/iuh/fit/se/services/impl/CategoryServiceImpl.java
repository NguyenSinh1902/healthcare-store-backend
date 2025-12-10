package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.category.CategoryResponseDTO;
import iuh.fit.se.entities.category.Category;
import iuh.fit.se.exceptions.NotFoundException;
import iuh.fit.se.mappers.CategoryMapper;
import iuh.fit.se.repositories.CategoryRepository;
import iuh.fit.se.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    //Get parent
    @Override
    public List<CategoryResponseDTO> getParentCategories() {
        List<Category> parents = categoryRepository.findByParentCategoryIdIsNull();
        if (parents.isEmpty()) {
            throw new NotFoundException("No parent directory was found.");
        }
        return parents.stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    //Get child category by parent ID
    @Override
    public List<CategoryResponseDTO> getSubCategories(Long parentId) {
        List<Category> subs = categoryRepository.findByParentCategoryId(parentId);
        if (subs.isEmpty()) {
            throw new NotFoundException("No child category for parent ID: " + parentId);
        }
        return subs.stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

}
