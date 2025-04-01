package app.giftCard.service;

import app.giftCard.model.GiftCard;
import app.giftCard.repository.GiftCardRepository;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateGiftCardRequest;
import org.springframework.stereotype.Service;

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
        GiftCard giftCard = GiftCard.builder()
                .name(createGiftCardRequest.getName())
                .value(createGiftCardRequest.getValue())
                .hardcoded(false)
                .build();

        giftCardRepository.save(giftCard);
    }

    public void resetGiftCards() {
        List<GiftCard> giftCards = giftCardRepository.findAll();

        for (GiftCard giftCard : giftCards) {
            if (!giftCard.isHardcoded()) {
                giftCardRepository.delete(giftCard);  // Изтриваме конкретната карта
            }
        }
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
