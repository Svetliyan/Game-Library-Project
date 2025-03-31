package app.giftCard;

import app.giftCard.model.GiftCard;
import app.giftCard.repository.GiftCardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

@Configuration
public class GiftCardSeeder {

    private static final Logger logger = Logger.getLogger(GiftCardSeeder.class.getName());

    @Bean
    CommandLineRunner seedGiftCards(GiftCardRepository giftCardRepository) {
        return args -> {
            List<GiftCard> giftCardList = List.of(
                    GiftCard.builder().name("Gift Card Bronze $10").value(BigDecimal.valueOf(10)).hardcoded(true).build(),
                    GiftCard.builder().name("Gift Card Silver $20").value(BigDecimal.valueOf(20)).hardcoded(true).build(),
                    GiftCard.builder().name("Gift Card Gold $50").value(BigDecimal.valueOf(50)).hardcoded(true).build()
            );


            for (GiftCard gc : giftCardList) {
                // Check if the gift card with the same name already exists
                if (!giftCardRepository.existsByName(gc.getName())) {
                    giftCardRepository.save(gc); // Save to the database if it doesn't exist
                    logger.info("Gift card saved: " + gc.getName());
                } else {
                    logger.info("Gift card already exists: " + gc.getName());
                }
            }
        };
    }
}
