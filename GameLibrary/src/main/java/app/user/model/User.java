package app.user.model;

import app.game.model.Game;
import app.game.model.PurchasedGame;
import app.giftCard.model.GiftCard;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    private BigDecimal balance;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private List<Game> games;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private List<PurchasedGame> purchasedGames;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_gift_cards",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "gift_card_id")
    )
    private List<GiftCard> purchasedGiftCards;
}
