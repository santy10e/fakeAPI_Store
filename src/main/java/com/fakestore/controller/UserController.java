package com.fakestore.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fakestore.JwtUtil;
import com.fakestore.model.User;
import com.fakestore.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "API para gestionar usuarios y autenticación")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Obtener el usuario autenticado", description = "Devuelve los detalles del usuario autenticado basado en el token proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario autenticado obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser(@RequestHeader("Authorization") String token) {
        try {
            String email = JwtUtil.validateToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(404).body(Map.of("message", "Usuario no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido"));
        }
    }

    @Operation(summary = "Registrar un usuario", description = "Registra un nuevo usuario en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Obtener un usuario por ID", description = "Devuelve los detalles de un usuario específico basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")

    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar un usuario por ID", description = "Elimina un usuario del sistema basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Iniciar sesión", description = "Inicia sesión con las credenciales del usuario y genera un token JWT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "404", description = "Correo no registrado"),
        @ApiResponse(responseCode = "401", description = "Contraseña incorrecta")
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        User existingUser = userService.findByEmail(user.getEmail());

        if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
            String token = JwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok().body(Map.of(
                    "token", token,
                    "message", "Login exitoso"));
        } else if (existingUser == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "message", "Correo no registrado"));
        } else {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "Contraseña incorrecta"));
        }
    }

}
