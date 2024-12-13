package com.fakestore.controller;

import com.fakestore.model.Category;
import com.fakestore.model.Product;
import com.fakestore.service.CategoryService;
import com.fakestore.service.ProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    // Rutas existentes
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.findAll();
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.save(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return categoryService.save(category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
    }

    // Nueva funcionalidad añadida
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @GetMapping("/{id}/products")
    public List<Product> getProductsByCategoryId(@PathVariable Long id) {
        return productService.findFilteredProducts(0, Integer.MAX_VALUE, null, null, null, null, id);
    }

    // [GET] /api/v1/categories/1/products
    @GetMapping("/categories/1/products")
    public ResponseEntity<?> getProductsByCategoryOne() {
        // Aquí puedes retornar una lista estática o dinámica de productos para la
        // categoría 1
        return ResponseEntity.ok("Lista de productos de la categoría 1");
    }

    // [GET] /api/v1/categories/1
    @GetMapping("/categories/1")
    public ResponseEntity<?> getCategoryOneDetails() {
        // Aquí puedes retornar los detalles de la categoría 1
        return ResponseEntity.ok("Detalles de la categoría 1");
    }

}
