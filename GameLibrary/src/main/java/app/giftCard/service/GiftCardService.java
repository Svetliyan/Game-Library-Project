package app.giftCard.service;

import app.giftCard.repository.GiftCardRepository;
import app.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class GiftCardService {

    private final GiftCardRepository giftCardRepository;
    private final UserService userService;

    public GiftCardService(GiftCardRepository giftCardRepository, UserService userService) {
        this.giftCardRepository = giftCardRepository;
        this.userService = userService;
    }


}
