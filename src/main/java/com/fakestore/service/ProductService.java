package com.fakestore.service;

import com.fakestore.model.Product;
import com.fakestore.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> findFilteredProducts(
            int offset,
            int limit,
            String title,
            Double price,
            Double price_min,
            Double price_max,
            Long categoryId
    ) {
        List<Product> products = productRepository.findAll();

        if (title != null) {
            products = products.stream()
                    .filter(product -> product.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (price != null) {
            products = products.stream()
                    .filter(product -> product.getPrice().equals(price))
                    .collect(Collectors.toList());
        }

        if (price_min != null && price_max != null) {
            products = products.stream()
                    .filter(product -> product.getPrice() >= price_min && product.getPrice() <= price_max)
                    .collect(Collectors.toList());
        }

        if (categoryId != null) {
            products = products.stream()
                    .filter(product -> product.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        int start = Math.min(offset, products.size());
        int end = Math.min(start + limit, products.size());
        return products.subList(start, end);
    }
}
