//package app.web;
//
//import app.category.model.Category;
//import app.giftCard.GiftCardSeeder;
//import app.giftCard.model.GiftCard;
//import app.giftCard.service.GiftCardService;
//import app.security.AuthenticationDetails;
//import app.user.model.User;
//import app.user.service.UserService;
//import app.web.dto.CreateCategoryRequest;
//import app.web.dto.CreateGameRequest;
//import app.web.dto.CreateGiftCardRequest;
//import app.web.dto.RedeemGiftCardRequest;
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//@Controller
//@RequestMapping("/giftcards")
//public class GiftCardController {
//    private final UserService userService;
//    private final GiftCardService giftCardService;
//
//    @Autowired
//    public GiftCardController(UserService userService, GiftCardService giftCardService) {
//        this.userService = userService;
//        this.giftCardService = giftCardService;
//    }
//
//    @GetMapping("/redeem")
//    public ModelAndView getRedeemPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
//        User user = userService.getById(authenticationDetails.getId());
//
//        List<GiftCard> giftCards = giftCardService.getAllGiftCards(); // Взимаме картите от базата
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("redeem-giftcard");
//        modelAndView.addObject("user", user);
//        modelAndView.addObject("giftCards", giftCards); // Добавяме gift картите в ModelAndView
//
//        return modelAndView;
//    }
//
//    @PostMapping("/redeem")
//    @ResponseBody
//    public ResponseEntity<?> redeemGiftCard(@RequestBody Map<String, String> request, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
//        if (authenticationDetails == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "User not logged in"));
//        }
//
//        UUID userId = authenticationDetails.getId();
//        User user = userService.getById(userId);
//        UUID giftCardId = UUID.fromString(request.get("giftCardId"));
//
//        GiftCard giftCard = giftCardService.getGiftCardById(giftCardId);
//        if (giftCard == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "Invalid gift card"));
//        }
//
//        // Добавяне на стойността на картата към баланса на потребителя
//        user.setBalance(user.getBalance().add(giftCard.getValue()));
//        userService.save(user);
//
//        return ResponseEntity.ok(Map.of("success", true, "newBalance", user.getBalance()));
//    }
//
//    @GetMapping("/add")
//    public ModelAndView getAddGiftCardPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
//        User user = userService.getById(authenticationDetails.getId());
//
//        ModelAndView modelAndView = new ModelAndView("add-giftcard");
//        modelAndView.addObject("user", user);
//        modelAndView.addObject("createGiftCardRequest", new CreateGiftCardRequest());
//
//        return modelAndView;
//    }
//
//    @PostMapping
//    public String createNewGiftCard(@ModelAttribute("createGiftCardRequest") @Valid CreateGiftCardRequest createGiftCardRequest,
//                                BindingResult bindingResult,
//                                @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
//        User user = userService.getById(authenticationDetails.getId());
//
//        if (bindingResult.hasErrors()) {
//            return "add-giftcard";
//        }
//
//        giftCardService.createGiftCard(createGiftCardRequest, user);
//        return "redirect:/profile";
//    }
//}
