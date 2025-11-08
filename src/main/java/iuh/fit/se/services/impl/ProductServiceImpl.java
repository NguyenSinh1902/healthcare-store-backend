package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.product.ProductDetailDTO;
import iuh.fit.se.dtos.product.ProductRequestDTO;
import iuh.fit.se.dtos.product.ProductResponseDTO;
import iuh.fit.se.entities.category.Category;
import iuh.fit.se.entities.product.Product;
import iuh.fit.se.entities.product.ProductGroup;
import iuh.fit.se.exceptions.NotFoundException;
import iuh.fit.se.mappers.ProductMapper;
import iuh.fit.se.repositories.CategoryRepository;
import iuh.fit.se.repositories.ProductRepository;
import iuh.fit.se.services.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    //Get All
    @Override
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new NotFoundException("No products found");
        }
        return products.stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    //Create product
    @Transactional
    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        Category category = categoryRepository.findById(dto.getIdCategory())
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + dto.getIdCategory()));

        Product product = productMapper.toEntity(dto);
        product.setCategory(category);

        productRepository.save(product);
        return productMapper.toResponseDTO(product);
    }

    //Update product
    @Transactional
    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));

        // Update fields (if provided)
        if (dto.getNameProduct() != null) product.setNameProduct(dto.getNameProduct());
        if (dto.getBrand() != null) product.setBrand(dto.getBrand());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getOldPrice() != null) product.setOldPrice(dto.getOldPrice());
        if (dto.getDiscountPercent() != null) product.setDiscountPercent(dto.getDiscountPercent());
        if (dto.getStockQuantity() != null) product.setStockQuantity(dto.getStockQuantity());
        if (dto.getImageProduct() != null) product.setImageProduct(dto.getImageProduct());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getInformation() != null) product.setInformation(dto.getInformation());
        if (dto.getProductGroup() != null)
            product.setProductGroup(Enum.valueOf(ProductGroup.class, dto.getProductGroup()));

        if (dto.getIdCategory() != null) {
            Category category = categoryRepository.findById(dto.getIdCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found with ID: " + dto.getIdCategory()));
            product.setCategory(category);
        }

        Product updated = productRepository.save(product);
        return productMapper.toResponseDTO(updated);
    }

    //Delete product
    @Transactional
    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));
        productRepository.delete(product);
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
