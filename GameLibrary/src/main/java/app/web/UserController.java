package app.web;

import app.game.model.Game;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.UserDetailsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ModelAndView getProfilePage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getId());
        ModelAndView modelAndView = new ModelAndView("profile");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/edit")
    public ModelAndView getEditUserProfilePage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        UserDetailsRequest userDetailsRequest = new UserDetailsRequest();
        userDetailsRequest.setUsername(user.getUsername());
        userDetailsRequest.setEmail(user.getEmail());
        userDetailsRequest.setImg_url(user.getImg_url());
        userDetailsRequest.setBalance(user.getBalance());

        ModelAndView modelAndView = new ModelAndView("edit-profile");
        modelAndView.addObject("userDetailsRequest", userDetailsRequest);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PutMapping("/edit")
    public String edit(@AuthenticationPrincipal AuthenticationDetails authenticationDetails, UserDetailsRequest userDetailsRequest) {
        userService.updateDetails(authenticationDetails.getId(), userDetailsRequest);
        return "redirect:/profile";
    }
}
