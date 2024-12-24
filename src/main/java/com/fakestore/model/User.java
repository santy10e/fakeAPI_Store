package com.fakestore.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tratamiento;

    private String nombre;

    private String apellido;

    private String email;

    private String password;

    @Column(name = "recibir_ofertas")
    private boolean recibirOfertas;

    @Column(name = "suscribirse_boletin")
    private boolean suscribirseBoletin;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getUsername() {
        String username = nombre;
        return username;
    }
}
