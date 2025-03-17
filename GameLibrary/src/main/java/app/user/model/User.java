package app.user.model;

import app.game.model.Game;
import app.game.model.WishedGames;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {
    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private String img_url;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private List<Game> games;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private List<WishedGames> wishedGames;
}
