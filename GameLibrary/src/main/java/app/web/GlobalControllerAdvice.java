package app.web;

import app.security.AuthenticationDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("isAuthenticated")
    public boolean addAuthenticationDetails(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        return authenticationDetails != null && authenticationDetails.getId() != null;
    }
}
