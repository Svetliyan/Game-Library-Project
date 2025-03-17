package app.web;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.game.service.GameService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateCategoryRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final GameService gameService;
    private final UserService userService;

    @Autowired
    public CategoryController(CategoryService categoryService, GameService gameService, UserService userService) {
        this.categoryService = categoryService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping("/add")
    public ModelAndView getAddCategoryPage(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-category");
        modelAndView.addObject("user", user);
        modelAndView.addObject("createCategoryRequest", new CreateCategoryRequest());

        return modelAndView;
    }

    @PostMapping
    public String createNewCategory(@Valid CreateCategoryRequest createCategoryRequest, BindingResult bindingResult, HttpSession session){
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        if (bindingResult.hasErrors()) {
            return "add-category";
        }

        categoryService.createCategory(createCategoryRequest, user);

        return "redirect:/index";
    }
}
