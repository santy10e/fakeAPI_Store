package com.fakestore.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
// Genera getters y setters para todos los campos
@Getter
@Setter
// Genera un constructor sin argumentos (necesario para JPA)
@NoArgsConstructor
// Opcional: genera un constructor con todos los argumentos (útil en tests o cuando necesites construir objetos completos)
@AllArgsConstructor
// Evita la recursión en toString() al intentar imprimir la lista items
@ToString(exclude = "items")
// Excluye la lista items de equals y hashCode para evitar comparar colecciones grandes
@EqualsAndHashCode(of = {"id", "userId"})
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CartItem> items = new ArrayList<>();
}
