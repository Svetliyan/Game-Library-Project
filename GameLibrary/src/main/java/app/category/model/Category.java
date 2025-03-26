package app.category.model;

import app.game.model.Game;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @NotNull
    private String name;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "category")
    private List<Game> games;
}
