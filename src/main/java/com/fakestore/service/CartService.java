package com.fakestore.service;

import org.springframework.stereotype.Service;

import com.fakestore.CartItemDTO;
import com.fakestore.model.Cart;
import com.fakestore.model.CartItem;
import com.fakestore.model.Product;
import com.fakestore.repository.CartItemRepository;
import com.fakestore.repository.CartRepository;
import com.fakestore.repository.ProductRepository;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository,
            CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        // Recuperar el carrito por el ID del usuario
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario con ID: " + userId));
    
        // Recuperar el producto por su ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));
    
        // Buscar si el producto ya existe en el carrito
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    
        if (existingItem != null) {
            // Si el producto ya existe, aumenta la cantidad
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setUpdatedAt(new Date()); // Actualiza la fecha de modificación
        } else {
            // Si no existe, crea un nuevo ítem y agrégalo al carrito
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setCreationAt(new Date()); // Establece la fecha de creación
            newItem.setUpdatedAt(new Date());  // Establece la fecha de modificación
            cart.getItems().add(newItem);
        }
    
        // Guarda y devuelve el carrito actualizado
        return cartRepository.save(cart);
    }
    

    public Cart removeItemFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario con ID: " + userId));
        System.out.println("Carrito encontrado: " + cart);
    
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));
        System.out.println("Producto encontrado para eliminar: " + itemToRemove);
    
        cart.getItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);
        System.out.println("Producto eliminado del carrito");
    
        return cartRepository.save(cart);
    }
    
    

    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public Cart syncCart(Long userId, List<CartItemDTO> items) {
        System.out.println("Items recibidos: " + items);
        System.out.println("UserId extraído del token: " + userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario"));

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

    @Transactional
    public Cart updateItemQuantity(Long userId, Long productId, int newQuantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario con ID: " + userId));

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        if (newQuantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(newQuantity);
        }

        return cartRepository.save(cart);
    }

}
