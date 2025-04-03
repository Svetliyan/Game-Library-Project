package app.web;

import app.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public String handleUsernameAlreadyExist(HttpServletRequest request, RedirectAttributes redirectAttributes, UsernameAlreadyExistException exception) {
        String message = exception.getMessage();

        redirectAttributes.addFlashAttribute("usernameAlreadyExistMessage", message);
        return "redirect:/register";
    }

    @ExceptionHandler(TakenUsernameException.class)
    public String handleTakenUsername(HttpServletRequest request, RedirectAttributes redirectAttributes, TakenUsernameException exception) {
        String message = exception.getMessage();

        redirectAttributes.addFlashAttribute("takenUsernameException", message);
        return "redirect:/edit";
    }

    @ExceptionHandler(UsernameLengthException.class)
    public ModelAndView handleUsernameLength(HttpServletRequest request, RedirectAttributes redirectAttributes, UsernameLengthException exception) {
        String message = exception.getMessage();
        ModelAndView modelAndView = new ModelAndView("internal-server-error");
        modelAndView.addObject("usernameLengthException", message);
        return modelAndView;
    }

//    @ExceptionHandler(CategoryAlreadyExistException.class)
//    public String handleCategoryAlreadyExist(HttpServletRequest request, RedirectAttributes redirectAttributes, CategoryAlreadyExistException exception) {
//        String message = exception.getMessage();
//
//        redirectAttributes.addFlashAttribute("categoryAlreadyExistMessage", message);
//        return "redirect:/categories/add";
//    }

    @ExceptionHandler(CategoryAlreadyExistException.class)
    public ModelAndView handleCategoryAlreadyExist(HttpServletRequest request, RedirectAttributes redirectAttributes, CategoryAlreadyExistException exception) {
        String message = exception.getMessage();

        ModelAndView modelAndView = new ModelAndView("internal-server-error");
        modelAndView.addObject("categoryAlreadyExistException", message);

        return modelAndView;
    }

//    @ExceptionHandler(GameAlreadyExistException.class)
//    public String handleGameAlreadyExist(HttpServletRequest request, RedirectAttributes redirectAttributes, GameAlreadyExistException exception) {
//        String message = exception.getMessage();
//
//        redirectAttributes.addFlashAttribute("gameAlreadyExistMessage", message);
//        return "redirect:/games/add";
//    }

    @ExceptionHandler(GameAlreadyExistException.class)
    public ModelAndView handleGameAlreadyExist(HttpServletRequest request, RedirectAttributes redirectAttributes, GameAlreadyExistException exception) {
        String message = exception.getMessage();

        ModelAndView modelAndView = new ModelAndView("internal-server-error");
        modelAndView.addObject("gameAlreadyExistException", message);

        return modelAndView;
    }

    @ExceptionHandler(TakenTitleException.class)
    public String handleTakenTitle(HttpServletRequest request, RedirectAttributes redirectAttributes, TakenTitleException exception) {
        String message = exception.getMessage();

        redirectAttributes.addFlashAttribute("takenTitleException", message);
        return "redirect:/profile";
    }

    @ExceptionHandler(NotEnoughMoney.class)
    public ModelAndView handleNotEnoughMoney(HttpServletRequest request, RedirectAttributes redirectAttributes, NotEnoughMoney exception) {
        String message = exception.getMessage();

        ModelAndView modelAndView = new ModelAndView("internal-server-error");
        modelAndView.addObject("notEnoughMoney", message);

        return modelAndView;
    }

    @ExceptionHandler(DomainException.class)
    public ModelAndView handleDomainException(HttpServletRequest request, RedirectAttributes redirectAttributes, DomainException exception) {
        String message = exception.getMessage();

        ModelAndView modelAndView = new ModelAndView("internal-server-error");
        modelAndView.addObject("domainException", message);

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AccessDeniedException.class,
            NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class
    })
    public ModelAndView handleNotFoundExceptions(Exception exception) {

        return new ModelAndView("not-found");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception exception) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("internal-server-error");
        modelAndView.addObject("errorMessage", exception.getClass().getSimpleName());

        return modelAndView;
    }
}
