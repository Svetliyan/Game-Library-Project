package app.scheduler;

import app.giftCard.service.GiftCardService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GiftCardResetScheduler {

    private final GiftCardService giftCardService;

    public GiftCardResetScheduler(GiftCardService giftCardService) {
        this.giftCardService = giftCardService;
    }

    // Изпълнява се на всеки 7 дни
    @Scheduled(fixedRate = 604800000)  // 604800000 милисекунди = 7 дни
    public void resetGiftCards() {
        giftCardService.resetGiftCards();
        System.out.println("Non-hardcoded gift cards have been reset.");
    }
}
