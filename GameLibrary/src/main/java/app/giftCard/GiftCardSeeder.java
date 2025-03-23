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
                    GiftCard.builder().name("Gift Card Bronze $10").value(BigDecimal.valueOf(10)).img_url("https://gamecardsdirect.com/content/picture/11175/steam-10-dollar-us-nl.webp").build(),
                    GiftCard.builder().name("Gift Card Silver $20").value(BigDecimal.valueOf(20)).img_url("https://products.eneba.games/resized-products/-hi26rGSlG5oeVN4mxhBi-kpL02jHZ1mE8RvfcGwC9U_1920x1080_1x-0.jpeg").build(),
                    GiftCard.builder().name("Gift Card Gold $50").value(BigDecimal.valueOf(50)).img_url("https://m.media-amazon.com/images/I/815jDkv65fL.jpg").build()
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
