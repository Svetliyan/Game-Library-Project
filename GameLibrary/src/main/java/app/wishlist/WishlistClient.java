package app.wishlist;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "wishlist-svc", url = "http://localhost:8081")
public interface WishlistClient {
    @PostMapping("/wishlist")
    WishlistItemDTO addItemToWishlist(@RequestBody WishlistItemDTO item);

    @GetMapping("/wishlist/{userId}")
    List<WishlistItemDTO> getUserWishlist(@PathVariable UUID userId);
}
