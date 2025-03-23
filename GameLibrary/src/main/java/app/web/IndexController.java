package app.web;

import app.game.model.Game;
import app.game.service.GameService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import app.web.dto.UserDetailsRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class IndexController {

    private final UserService userService;
    private final GameService gameService;

    public IndexController(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @GetMapping("/index")
    public String getHomePage() {

        return "index";
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public String processRegisterRequest(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/register";
        }

        userService.registerUser(registerRequest);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        return modelAndView;
    }

    @PostMapping("/login")
    public String processLoginRequest(@Valid LoginRequest loginRequest, BindingResult bindingResult, HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        User user = userService.loginUser(loginRequest);

        // Когато се логвам - активирам сесия и поставям в тази сесия ID-то на потребителя!!!
        session.setAttribute("user_id", user.getId());

        return "redirect:/store";
    }

    @GetMapping("/profile")
    public ModelAndView getProfilePage(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/edit")
    public ModelAndView getEditPage(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        if (userId == null) {
            ModelAndView modelAndView2 = new ModelAndView();
            modelAndView2.setViewName("redirect:/login");
            return modelAndView2;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.addObject("userDetailsRequest", new UserDetailsRequest());
        modelAndView.setViewName("edit-profile");

        return modelAndView;
    }

    @PutMapping("/edit")
    public String edit(UserDetailsRequest userDetailsRequest, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        userService.updateDetails(userId,userDetailsRequest);

        return "redirect:/home";
    }

    @GetMapping("/store")
    public ModelAndView getStorePage(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        List<Game> allSystemGames = gameService.getAllGames();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("store");
        modelAndView.addObject("user", user);
        modelAndView.addObject("allSystemGames", allSystemGames);

        return modelAndView;
    }

    @GetMapping("/logout")
    public String getLogout(HttpSession session) {
        session.invalidate();

        return "redirect:/index";
    }
}
