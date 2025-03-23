package app.giftCard.repository;

import app.giftCard.model.GiftCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard, UUID> {
    boolean existsByName(String name);
}
