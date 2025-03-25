package app.web;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.game.model.Game;
import app.game.repository.GameRepository;
import app.game.service.GameService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateGameRequest;
import app.security.AuthenticationDetails;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    public GameController(UserService userService,
                          GameService gameService,
                          GameRepository gameRepository,
                          CategoryService categoryService) {
        this.userService = userService;
        this.gameService = gameService;
        this.gameRepository = gameRepository;
        this.categoryService = categoryService;
    }

    @GetMapping("/add")
    public ModelAndView getAddGamePage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getId());
        List<Category> categories = categoryService.getAllCategories();

        ModelAndView modelAndView = new ModelAndView("add-game");
        modelAndView.addObject("user", user);
        modelAndView.addObject("createGameRequest", new CreateGameRequest());
        modelAndView.addObject("categories", categories);

        return modelAndView;
    }

    @PostMapping
    public String createNewGame(@ModelAttribute("createGameRequest") @Valid CreateGameRequest createGameRequest,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getId());

        if (bindingResult.hasErrors()) {
            return "add-game";
        }

        gameService.createGame(createGameRequest, user);
        return "redirect:/store";
    }

    @GetMapping("games/{id}")
    public ModelAndView getGamePage(@PathVariable UUID id,
                                    @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getId());
        Game game = gameService.getById(id);

        ModelAndView modelAndView = new ModelAndView("games");
        boolean alreadyPurchased = gameService.isGamePurchased(id, user);
        modelAndView.addObject("game", game);
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
    public String purchaseGame(@PathVariable UUID id,
                               @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getId());
        gameService.purchaseGame(id, user);
        return "redirect:/games/library";
    }

    @GetMapping("/library")
    public ModelAndView getLibraryPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getId());
        ModelAndView modelAndView = new ModelAndView("library");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @DeleteMapping("/delete/{id}")
    public String deleteGame(@PathVariable UUID id) {
        gameService.deleteById(id);
        return "redirect:/store";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getEditGamePage(@PathVariable UUID id,
                                        @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getId());
        if (user == null) {
            return new ModelAndView("redirect:/login");
        }

        Game game = gameService.getById(id);
        if (game == null) {
            throw new RuntimeException("Game not found");
        }

        // Ако категорията е null, задаваме дефолтна категория
        if (game.getCategory() == null) {
            game.setCategory(new Category());
        }

        // Попълване на CreateGameRequest с текущите стойности от играта
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

        List<Category> categories = categoryService.getAllCategories();

        ModelAndView modelAndView = new ModelAndView("edit-game");
        modelAndView.addObject("game", game);
        modelAndView.addObject("createGameRequest", createGameRequest);
        modelAndView.addObject("categories", categories);

        return modelAndView;
    }
}
