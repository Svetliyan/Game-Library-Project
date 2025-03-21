package app.game.repository;

import app.game.model.PurchasedGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchasedGameRepository extends JpaRepository<PurchasedGame, UUID> {
}
