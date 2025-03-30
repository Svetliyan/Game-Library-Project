package app.wishlist;

import lombok.Data;

import java.util.UUID;

@Data
public class WishlistItemDTO {
    private UUID id;
    private UUID userId;
    private UUID gameId;
    private String mainImgUrl;
}
