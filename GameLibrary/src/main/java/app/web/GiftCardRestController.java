package app.web;

import app.giftCard.model.GiftCard;
import app.giftCard.service.GiftCardService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateGiftCardRequest;
import app.web.dto.RedeemGiftCardRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/giftcards")
public class GiftCardRestController {
    private final UserService userService;
    private final GiftCardService giftCardService;

    @Autowired
    public GiftCardRestController(UserService userService, GiftCardService giftCardService) {
        this.userService = userService;
        this.giftCardService = giftCardService;
    }

//     Връща всички карти (използва се за динамично зареждане с JavaScript)
    @GetMapping
    public ResponseEntity<List<GiftCard>> getAllGiftCards() {
        return ResponseEntity.ok(giftCardService.getAllGiftCards());
    }

    // Осребряване на карта (изпраща се AJAX заявка)
    @PostMapping("/redeem")
    public ResponseEntity<?> redeemGiftCard(@RequestBody RedeemGiftCardRequest request,
                                            @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        if (authenticationDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "User not logged in"));
        }

        UUID userId = authenticationDetails.getId();
        User user = userService.getById(userId);

        UUID giftCardId;
        try {
            giftCardId = UUID.fromString(request.getGiftCardId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid gift card ID format"));
        }

        GiftCard giftCard = giftCardService.getGiftCardById(giftCardId);
        if (giftCard == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid gift card"));
        }

        // Добавяне на стойността на картата към баланса на потребителя
        user.setBalance(user.getBalance().add(giftCard.getValue()));
        userService.save(user);

        return ResponseEntity.ok(Map.of("success", true, "newBalance", user.getBalance()));
    }

    // Създаване на нова подаръчна карта
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createNewGiftCard(@RequestBody @Valid CreateGiftCardRequest createGiftCardRequest,
                                               BindingResult bindingResult,
                                               @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        if (authenticationDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "User not logged in"));
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid data"));
        }

        User user = userService.getById(authenticationDetails.getId());
        giftCardService.createGiftCard(createGiftCardRequest, user);

        return ResponseEntity.ok(Map.of("success", true, "redirectUrl", "/profile"));
    }
}


