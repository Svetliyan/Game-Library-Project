package app.wishlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WishlistClientService {

    @Autowired
    private WishlistClient wishlistClient;

    public WishlistItemDTO addToWishlist(WishlistItemDTO item) {
        return wishlistClient.addItemToWishlist(item);
    }

    public List<WishlistItemDTO> getUserWishlist(UUID userId) {
        return wishlistClient.getUserWishlist(userId);
    }
}
