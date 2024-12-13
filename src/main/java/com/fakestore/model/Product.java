package com.fakestore.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Double price;
    private String description;

    @Convert(converter = JsonToListConverter.class)
    private List<String> images;

    @ManyToOne
    private Category category;

    private LocalDateTime creationAt;
    private LocalDateTime updatedAt;
}
