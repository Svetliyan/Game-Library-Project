package app.web;

import app.giftCard.model.GiftCard;
import app.giftCard.service.GiftCardService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateGiftCardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/giftcards")
public class GiftCardWebController {
    private final UserService userService;
    private final GiftCardService giftCardService;

    @Autowired
    public GiftCardWebController(UserService userService, GiftCardService giftCardService) {
        this.userService = userService;
        this.giftCardService = giftCardService;
    }

    @GetMapping("/redeem")
    public ModelAndView getRedeemPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        if (authenticationDetails == null) {
            return new ModelAndView("redirect:/login");
        }

        User user = userService.getById(authenticationDetails.getId());
        List<GiftCard> giftCards = giftCardService.getAllGiftCards();

        ModelAndView modelAndView = new ModelAndView("redeem-giftcard");
        modelAndView.addObject("user", user);
        modelAndView.addObject("giftCards", giftCards);
        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView getAddGiftCardPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getId());

        ModelAndView modelAndView = new ModelAndView("add-giftcard");
        modelAndView.addObject("user", user);
        modelAndView.addObject("createGiftCardRequest", new CreateGiftCardRequest()); // Подавате този обект

        return modelAndView;
    }

}

