package app.web;

import app.game.model.Game;
import app.game.service.GameService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class IndexController {

    private final UserService userService;
    private final GameService gameService;

    public IndexController(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @GetMapping("/index")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        boolean isAuthenticated = authenticationDetails != null;

        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("isAuthenticated", isAuthenticated);

        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());
        return modelAndView;
    }

    @PostMapping("/register")
    public String processRegisterRequest(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        userService.registerUser(registerRequest);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) String errorParam) {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        if (errorParam != null) {
            modelAndView.addObject("errorMessage", "Incorrect username or password!");
        }

        return modelAndView;
    }

    @GetMapping("/store")
    public ModelAndView getStorePage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getId());
        List<Game> allSystemGames = gameService.getAllGames();

        ModelAndView modelAndView = new ModelAndView("store");
        modelAndView.addObject("user", user);
        modelAndView.addObject("allSystemGames", allSystemGames);

        boolean isAuthenticated = authenticationDetails != null;
        modelAndView.addObject("isAuthenticated", isAuthenticated);

        return modelAndView;
    }
}