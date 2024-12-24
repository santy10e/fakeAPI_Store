package com.fakestore.controller;

import com.fakestore.model.Category;
import com.fakestore.model.Product;
import com.fakestore.service.CategoryService;
import com.fakestore.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Category API", description = "API para gestionar categorías y productos relacionados")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @Operation(summary = "Obtener todas las categorías", description = "Devuelve una lista de todas las categorías disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente")
    })
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.findAll();
    }

    @Operation(summary = "Crear una nueva categoría", description = "Crea una nueva categoría con los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.save(category);
    }

    @Operation(summary = "Actualizar una categoría", description = "Actualiza los datos de una categoría existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return categoryService.save(category);
    }

    @Operation(summary = "Eliminar una categoría", description = "Elimina una categoría existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
    }

    @Operation(summary = "Obtener una categoría por ID", description = "Devuelve los datos de una categoría específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @Operation(summary = "Obtener productos por ID de categoría", description = "Devuelve una lista de productos asociados a una categoría específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/{id}/products")
    public List<Product> getProductsByCategoryId(@PathVariable Long id) {
        return productService.findFilteredProducts(0, Integer.MAX_VALUE, null, null, null, null, id);
    }

    @Operation(summary = "Obtener productos de la categoría 1", description = "Devuelve una lista de productos estática o dinámica para la categoría 1.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    })
    @GetMapping("/categories/1/products")
    public ResponseEntity<?> getProductsByCategoryOne() {
        // Aquí puedes retornar una lista estática o dinámica de productos para la
        // categoría 1
        return ResponseEntity.ok("Lista de productos de la categoría 1");
    }

    @Operation(summary = "Obtener detalles de la categoría 1", description = "Devuelve los detalles de la categoría 1.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles de la categoría obtenidos exitosamente")
    })
    @GetMapping("/categories/1")
    public ResponseEntity<?> getCategoryOneDetails() {
        // Aquí puedes retornar los detalles de la categoría 1
        return ResponseEntity.ok("Detalles de la categoría 1");
    }

}
