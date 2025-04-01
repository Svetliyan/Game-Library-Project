package app.web;

import app.game.model.Game;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.UserDetailsRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

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
    public String edit(@AuthenticationPrincipal AuthenticationDetails authenticationDetails, @Valid UserDetailsRequest userDetailsRequest, BindingResult bindingResult) {
        userService.updateDetails(authenticationDetails.getId(), userDetailsRequest);

        return "redirect:/profile";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        List<User> users = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", users);

        return modelAndView;
    }

    @PutMapping("/{id}/status") // PUT /users/{id}/status
    @PreAuthorize("hasRole('ADMIN')")
    public String switchUserStatus(@PathVariable UUID id) {

        userService.switchStatus(id);

        return "redirect:/users";
    }

    @PutMapping("/{id}/role") // PUT /users/{id}/role
    @PreAuthorize("hasRole('ADMIN')")
    public String switchUserRole(@PathVariable UUID id) {

        userService.switchRole(id);

        return "redirect:/users";
    }
}
