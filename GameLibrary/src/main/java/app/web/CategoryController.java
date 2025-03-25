package app.web;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateCategoryRequest;
import app.security.AuthenticationDetails;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAddCategoryPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getId());

        ModelAndView modelAndView = new ModelAndView("add-category");
        modelAndView.addObject("user", user);
        modelAndView.addObject("createCategoryRequest", new CreateCategoryRequest());

        return modelAndView;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createNewCategory(@Valid CreateCategoryRequest createCategoryRequest,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getId());

        if (bindingResult.hasErrors()) {
            return "add-category";
        }

        categoryService.createCategory(createCategoryRequest, user);
        return "redirect:/index";
    }

    @GetMapping("/library")
    public ModelAndView getLibraryPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getId());

        ModelAndView modelAndView = new ModelAndView("library");
        modelAndView.addObject("user", user);
        return modelAndView;
    }
}
