package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @Autowired
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping
    public WishlistItem addToWishlist(@RequestBody WishlistItem wishlistItem) {
        return wishlistService.addWishlistItem(wishlistItem);
    }

    @GetMapping("/{userId}")
    public List<WishlistItem> getWishlist(@PathVariable UUID userId) {
        return wishlistService.getWishlistByUserId(userId);
    }
}
