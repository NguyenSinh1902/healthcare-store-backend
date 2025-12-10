package iuh.fit.se.repositories;

import iuh.fit.se.entities.product.Product;
import iuh.fit.se.entities.product.ProductGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //Get all products by group
    List<Product> findByProductGroup(ProductGroup productGroup);


    //Get all products by category ID
    List<Product> findByCategory_IdCategory(Long categoryId);

    //Find product by name
    Optional<Product> findByNameProduct(String nameProduct);

    //Filter products by price range and brand (if available)
    @Query("""
        SELECT p FROM Product p
        WHERE (:brand IS NULL OR p.brand = :brand)
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:categoryId IS NULL OR p.category.idCategory = :categoryId)
        """)
    List<Product> filterProducts(
            @Param("brand") String brand,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("categoryId") Long categoryId
    );
}
