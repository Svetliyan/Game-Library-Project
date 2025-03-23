package app.web;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.game.model.Game;
import app.game.repository.GameRepository;
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

    private final GameRepository gameRepository;
    private final CategoryService categoryService;

    @Autowired
    public GameController(UserService userService, GameService gameService, GameRepository gameRepository, CategoryService categoryService) {
        this.userService = userService;
        this.gameService = gameService;
        this.gameRepository = gameRepository;
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

        boolean alreadyPurchased = gameService.isGamePurchased(id, user);
        modelAndView.addObject("alreadyPurchased", alreadyPurchased);

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getGameById(@PathVariable UUID id) {
        Game game = gameService.getById(id);

        ModelAndView modelAndView = new ModelAndView("games");
        modelAndView.addObject("game", game);

        return modelAndView;
    }

    @PostMapping("/purchase/{id}")
    public String purchaseGame(@PathVariable UUID id, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");

        if (userId == null) {
            return "redirect:/login"; // Ако няма потребител в сесията, пренасочваме към логин
        }

        User user = userService.getById(userId);
        Game game = gameService.getById(id);

        gameService.purchaseGame(id, user);

        return "redirect:/games/library";
    }

    @GetMapping("/library")
    public ModelAndView getLibraryPage(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("library");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @DeleteMapping("/delete/{id}")
    public String deleteGame(@PathVariable UUID id) {
        gameService.deleteById(id);


        return "redirect:/store";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getEditGamePage(@PathVariable UUID id, HttpSession session) {
        // Проверка дали потребителят е влезнал в системата
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        if (userId == null) {
            ModelAndView modelAndView2 = new ModelAndView();
            modelAndView2.setViewName("redirect:/login");
            return modelAndView2;
        }

        // Извличане на играта по ID
        Game game = gameService.getById(id);

        // Проверка дали играта съществува и има категория
        if (game == null) {
            throw new RuntimeException("Game not found");
        }

        // Ако категорията е null, можеш да зададеш стойност по подразбиране или да направиш обработка
        if (game.getCategory() == null) {
            game.setCategory(new Category()); // Може да зададеш дефолтна категория, ако не съществува
        }

        // Попълваме `createGameRequest` със стойности от съществуващата игра
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setTitle(game.getTitle());
        createGameRequest.setDescription(game.getDescription());
        createGameRequest.setStorage(game.getStorage());
        createGameRequest.setPrice(game.getPrice());
        createGameRequest.setCoverImage_url(game.getCoverImg_url());
        createGameRequest.setMainImg_url(game.getMainImg_url());
        createGameRequest.setFirstImage_url(game.getFirstImage_url());
        createGameRequest.setSecondImage_url(game.getSecondImage_url());
        createGameRequest.setThirdImage_url(game.getThirdImage_url());
        createGameRequest.setFourthImage_url(game.getFourthImage_url());
        createGameRequest.setCategory_id(game.getCategory().getId());

        // Извличане на всички категории
        List<Category> categories = categoryService.getAllCategories();

        // Подготовка на модела за Thymeleaf
        ModelAndView modelAndView = new ModelAndView("edit-game");
        modelAndView.addObject("game", game);
        modelAndView.addObject("createGameRequest", createGameRequest);
        modelAndView.addObject("categories", categories);

        return modelAndView;
    }
}
