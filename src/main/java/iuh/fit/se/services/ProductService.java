package iuh.fit.se.services;

import iuh.fit.se.dtos.product.ProductDetailDTO;
import iuh.fit.se.dtos.product.ProductRequestDTO;
import iuh.fit.se.dtos.product.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    //Get all products by group (for homepage)
    List<ProductResponseDTO> getProductsByGroup(String productGroup);

    //Get all products by category
    List<ProductResponseDTO> getProductsByCategory(Long categoryId);

    //Get product details by ID
    ProductDetailDTO getProductDetail(Long id);


    //ADMIN

    //Get All product
    List<ProductResponseDTO> getAllProducts();

    //Create product
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);

    //Update product
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO);

    //Delete product
    void deleteProduct(Long id);

    //filter Products
    List<ProductResponseDTO> filterProducts(String brand, Double minPrice, Double maxPrice, Long categoryId, String sort);


}
