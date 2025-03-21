package app.game.model;

import app.category.model.Category;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private String coverImg_url;

    @Column(nullable = false)
    private String mainImg_url;

    @Column(nullable = false)
    private String firstImage_url;

    @Column(nullable = false)
    private String secondImage_url;

    @Column(nullable = false)
    private String thirdImage_url;

    @Column(nullable = false)
    private String fourthImage_url;

    @ManyToOne //много игри ще принадлеждат на една категория.
    private Category category;

    @ManyToOne
    private User owner;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    private Boolean isVisible;
}
