package com.fakestore.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fakestore.CartItemDTO;
import com.fakestore.UpdateCartItemDTO;
import com.fakestore.model.Cart;
import com.fakestore.model.CartItem;
import com.fakestore.model.Product;
import com.fakestore.repository.CartRepository;
import com.fakestore.repository.ProductRepository;
import com.fakestore.service.CartService;
import com.fakestore.service.JwtService;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart API", description = "API para gestionar el carrito de compras")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Operation(summary = "Obtener el carrito de un usuario", description = "Devuelve el carrito del usuario especificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Eliminar un producto del carrito", description = "Elimina un producto del carrito del usuario especificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado del carrito exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{userId}/remove")
    public ResponseEntity<?> removeItem(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestHeader("Authorization") String token) {

        Long extractedUserId = jwtService.extractUserIdFromToken(token);
        if (!extractedUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            Cart updatedCart = cartService.removeItemFromCart(userId, productId);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar producto del carrito: " + e.getMessage());
        }
    }

    @Operation(summary = "Vaciar el carrito", description = "Elimina todos los productos del carrito del usuario especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito vaciado exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<?> clearCart(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {

        Long extractedUserId = jwtService.extractUserIdFromToken(token);
        if (!extractedUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok("Carrito vacío exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al vaciar el carrito: " + e.getMessage());
        }
    }

    @Operation(summary = "Agregar un producto al carrito", description = "Agrega un producto al carrito del usuario especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto agregado al carrito exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/{userId}/add")
    public ResponseEntity<?> addItem(
            @PathVariable Long userId,
            @RequestBody CartItemDTO requestBody,
            @RequestHeader("Authorization") String token) {

        Long extractedUserId = jwtService.extractUserIdFromToken(token);
        if (!extractedUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            extractedUserId = jwtService.extractUserIdFromToken(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        if (!extractedUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        Long productId = requestBody.getProductId();
        int quantity = requestBody.getQuantity();

        // Validación adicional
        if (productId == null || quantity <= 0) {
            return ResponseEntity.badRequest().body("productId y quantity deben ser válidos.");
        }

        try {
            Cart updatedCart = cartService.addItemToCart(userId, productId, quantity);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al agregar producto al carrito: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar la cantidad de un producto en el carrito", description = "Actualiza la cantidad de un producto existente en el carrito del usuario especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/{userId}/updateQuantity")
    public ResponseEntity<?> updateQuantity(
            @PathVariable Long userId,
            @RequestBody UpdateCartItemDTO updateRequest,
            @RequestHeader("Authorization") String token) {

        Long extractedUserId = jwtService.extractUserIdFromToken(token);
        if (!extractedUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        Long productId = updateRequest.getProductId();
        int newQuantity = updateRequest.getQuantity();

        // Validación
        if (productId == null || newQuantity < 0) {
            return ResponseEntity.badRequest().body("productId debe ser válido y quantity no puede ser negativo.");
        }

        try {
            if (newQuantity == 0) {
                cartService.removeItemFromCart(userId, productId);
                return ResponseEntity.ok("Producto eliminado del carrito.");
            } else {
                Cart updatedCart = cartService.updateItemQuantity(userId, productId, newQuantity);
                return ResponseEntity.ok(updatedCart);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la cantidad: " + e.getMessage());
        }
    }

    @Operation(summary = "Sincronizar el carrito", description = "Sincroniza los productos en el carrito del usuario especificado con una lista dada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito sincronizado exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Carrito o producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Cart syncCart(Long userId, List<CartItemDTO> items) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario"));

        // Limpia los ítems actuales del carrito antes de sincronizar
        cart.getItems().clear();

        for (CartItemDTO dto : items) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + dto.getProductId()));

            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(dto.getQuantity());
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @PostMapping("/{userId}/sync")
    public ResponseEntity<?> syncCart(
            @PathVariable Long userId,
            @RequestBody List<CartItemDTO> items,
            @RequestHeader("Authorization") String token) {

        Long extractedUserId = jwtService.extractUserIdFromToken(token);
        if (!extractedUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario con ID: " + userId));

        Map<Long, CartItem> existingItems = cart.getItems().stream()
                .collect(Collectors.toMap(item -> item.getProduct().getId(), item -> item));

        for (CartItemDTO dto : items) {
            try {
                if (existingItems.containsKey(dto.getProductId())) {
                    // Actualiza el producto si ya existe
                    CartItem existingItem = existingItems.get(dto.getProductId());
                    existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
                    existingItem.setUpdatedAt(new Date());
                } else {
                    // Si no existe, crea un nuevo ítem
                    Product product = productRepository.findById(dto.getProductId())
                            .orElseThrow(
                                    () -> new RuntimeException("Producto no encontrado con ID: " + dto.getProductId()));

                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(dto.getQuantity());
                    newItem.setCreationAt(new Date());
                    newItem.setUpdatedAt(new Date());

                    cart.getItems().add(newItem);
                }
            } catch (Exception e) {
                System.err.println(
                        "Error al procesar el producto con ID: " + dto.getProductId() + " - " + e.getMessage());
            }
        }

        // Guarda los cambios en el carrito
        Cart updatedCart = cartRepository.save(cart);

        return ResponseEntity.ok(updatedCart);
    }
}
