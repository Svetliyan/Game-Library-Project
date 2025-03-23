package app.giftCard.service;

import app.giftCard.model.GiftCard;
import app.giftCard.repository.GiftCardRepository;
import app.user.service.UserService;
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


    public List<GiftCard> getAllGiftCards() {
        return giftCardRepository.findAll();
    }

    public GiftCard getGiftCardById(UUID id) {
        return giftCardRepository.findById(id).orElse(null);
    }
}
