package app.giftCard.service;

import app.game.model.Game;
import app.game.model.PurchasedGame;
import app.giftCard.model.GiftCard;
import app.giftCard.repository.GiftCardRepository;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateGiftCardRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GiftCardService {

    private final GiftCardRepository giftCardRepository;
    private final UserService userService;

    public GiftCardService(GiftCardRepository giftCardRepository, UserService userService) {
        this.giftCardRepository = giftCardRepository;
        this.userService = userService;
    }

    public void createGiftCard(CreateGiftCardRequest createGiftCardRequest, User user) {
        if (createGiftCardRequest.getName() == null || createGiftCardRequest.getValue() == null) {
            throw new IllegalArgumentException("Gift card name or value cannot be null");
        }
        if (existsByName(createGiftCardRequest.getName())) {
            throw new IllegalStateException("Gift card with this name already exists");
        }

        GiftCard giftCard = GiftCard.builder()
                .name(createGiftCardRequest.getName())
                .value(createGiftCardRequest.getValue())
                .build();

        giftCardRepository.save(giftCard);
    }


    public List<GiftCard> getAllGiftCards() {
        return giftCardRepository.findAll();
    }

    public GiftCard getGiftCardById(UUID id) {
        return giftCardRepository.findById(id).orElse(null);
    }

    public Boolean existsByName(String name) {
        return giftCardRepository.existsByName(name);
    }
}
