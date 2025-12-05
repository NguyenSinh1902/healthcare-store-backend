package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.category.CategoryRequestDTO;
import iuh.fit.se.dtos.category.CategoryResponseDTO;
import iuh.fit.se.entities.category.Category;
import iuh.fit.se.exceptions.NotFoundException;
import iuh.fit.se.mappers.CategoryMapper;
import iuh.fit.se.repositories.CategoryRepository;
import iuh.fit.se.services.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    //add
    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        Category category = categoryMapper.toEntity(dto);
        categoryRepository.save(category);
        return categoryMapper.toResponseDTO(category);
    }

    //delete
    @Transactional
    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No category found with ID: " + id));

        //Khi xoa danh muc cha => set các danh muc con ve NULL thay vi xoa luon
        List<Category> subCategories = categoryRepository.findByParentCategoryId(id);
        if (!subCategories.isEmpty()) {
            for (Category sub : subCategories) {
                sub.setParentCategoryId(null);
            }
            categoryRepository.saveAll(subCategories);
        }

        categoryRepository.delete(category);
    }

    //update category
    @Transactional
    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id));

        // Update fields if not null
        if (dto.getNameCategory() != null) category.setNameCategory(dto.getNameCategory());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());
        if (dto.getImageCategory() != null) category.setImageCategory(dto.getImageCategory());
        category.setParentCategoryId(dto.getParentCategoryId()); // can set null or another parent ID

        Category updated = categoryRepository.save(category);
        return categoryMapper.toResponseDTO(updated);
    }
}
