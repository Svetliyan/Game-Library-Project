package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    public WishlistItem addWishlistItem(WishlistItem item) {
        return wishlistRepository.save(item);
    }

    public List<WishlistItem> getWishlistByUserId(UUID userId) {
        return wishlistRepository.findByUserId(userId);
    }
}
