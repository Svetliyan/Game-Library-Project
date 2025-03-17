package app.game.model;

import app.category.model.Category;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Game {
    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int storage;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String img_url;

    @ManyToOne //много игри ще принадлеждат на една категория.
    private Category category;

    @ManyToOne
    private User owner;
}
