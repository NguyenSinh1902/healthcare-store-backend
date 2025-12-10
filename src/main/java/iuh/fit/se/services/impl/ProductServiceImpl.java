package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.product.ProductDetailDTO;
import iuh.fit.se.dtos.product.ProductResponseDTO;
import iuh.fit.se.entities.product.Product;
import iuh.fit.se.entities.product.ProductGroup;
import iuh.fit.se.exceptions.NotFoundException;
import iuh.fit.se.mappers.ProductMapper;
import iuh.fit.se.repositories.CategoryRepository;
import iuh.fit.se.repositories.ProductRepository;
import iuh.fit.se.services.ProductService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    //Get Products By Group
    @Override
    public List<ProductResponseDTO> getProductsByGroup(String productGroup) {
        ProductGroup groupEnum;
        try {
            // Convert String ("FLASH_SALE") -> Enum (ProductGroup.FLASH_SALE)
            groupEnum = ProductGroup.valueOf(productGroup.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Invalid product group: " + productGroup);
        }

        List<Product> products = productRepository.findByProductGroup(groupEnum);
        if (products.isEmpty()) {
            throw new NotFoundException("No products found for group: " + groupEnum);
        }

        return products.stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


    //Get products by category
    @Override
    public List<ProductResponseDTO> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategory_IdCategory(categoryId);
        if (products.isEmpty()) {
            throw new NotFoundException("No products found for category ID: " + categoryId);
        }
        return products.stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    //Get product detail
    @Override
    public ProductDetailDTO getProductDetail(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));
        return productMapper.toDetailDTO(product);
    }

    //filter product
    @Override
    public List<ProductResponseDTO> filterProducts(String brand, Double minPrice, Double maxPrice, Long categoryId, String sort) {
        List<Product> products = productRepository.filterProducts(brand, minPrice, maxPrice, categoryId);

        if (products.isEmpty()) {
            throw new NotFoundException("No products found with given filters");
        }

        // Sort (Low to High / High to Low)
        if (sort != null) {
            switch (sort.toLowerCase()) {
                case "price_low_to_high" -> products.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
                case "price_high_to_low" -> products.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                default -> {} // "featured" or null: no sort
            }
        }

        return products.stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


}
