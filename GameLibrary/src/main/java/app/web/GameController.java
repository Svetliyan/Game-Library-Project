package app.web;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.game.model.Game;
import app.game.service.GameService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateGameRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/games")
public class GameController {

    private final UserService userService;
    private final GameService gameService;

    private final CategoryService categoryService;

    @Autowired
    public GameController(UserService userService, GameService gameService, CategoryService categoryService) {
        this.userService = userService;
        this.gameService = gameService;
        this.categoryService = categoryService;
    }

    @GetMapping("/add")
    public ModelAndView getAddGamePage(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        List<Category> categories = categoryService.getAllCategories();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-game");
        modelAndView.addObject("user", user);
        modelAndView.addObject("createGameRequest", new CreateGameRequest());
        modelAndView.addObject("categories", categories);

        return modelAndView;
    }

    @PostMapping
    public String createNewGame(@ModelAttribute("createGameRequest") @Valid CreateGameRequest createGameRequest, BindingResult bindingResult, HttpSession session){

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        if (bindingResult.hasErrors()) {
            return "/add-game";
        }

        gameService.createGame(createGameRequest, user);

        return "redirect:/store";
    }

    @GetMapping("games/{id}")
    public ModelAndView getGamePage(@PathVariable UUID id, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        Game game =  gameService.getById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("games");

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getGameById(@PathVariable UUID id) {
        Game game = gameService.getById(id);

        ModelAndView modelAndView = new ModelAndView("games");
        modelAndView.addObject("game", game);

        return modelAndView;
    }
}
