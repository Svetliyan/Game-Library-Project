package app.game.repository;

import app.game.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
    boolean existsByTitle(String title);
}
