package app.wishlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
public class WishlistController {
    private final WishlistClientService wishlistClientService;
    private final WishlistClient wishlistClient;

    @Autowired
    public WishlistController(WishlistClientService wishlistClientService, WishlistClient wishlistClient) {
        this.wishlistClientService = wishlistClientService;
        this.wishlistClient = wishlistClient;
    }

    @GetMapping("/wishlist")
    public String viewWishlist(Model model, @RequestParam UUID userId) {
        List<WishlistItemDTO> wishlist = wishlistClient.getUserWishlist(userId);
        model.addAttribute("wishlist", wishlist);
        return "wishlist";  // Тази стойност трябва да бъде името на HTML шаблона
    }


    @PostMapping("/wishlist/add/{userId}/{gameId}")
    public String addToWishlist(@PathVariable UUID userId, @PathVariable UUID gameId, @RequestParam String mainImgUrl) {
        WishlistItemDTO item = new WishlistItemDTO();
        item.setUserId(userId);
        item.setGameId(gameId);
        item.setMainImgUrl(mainImgUrl);
        wishlistClientService.addToWishlist(item);  // Използвай Feign клиента
        return "redirect:/profile";
    }
}
