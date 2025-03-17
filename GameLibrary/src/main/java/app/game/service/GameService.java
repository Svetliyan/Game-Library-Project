package app.game.service;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.game.model.Game;
import app.game.repository.GameRepository;
import app.user.model.User;
import app.web.dto.CreateGameRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final CategoryService categoryService;

    @Autowired
    public GameService(GameRepository gameRepository, CategoryService categoryService) {
        this.gameRepository = gameRepository;
        this.categoryService = categoryService;
    }

    public void createGame(CreateGameRequest createGameRequest, User user) {
        Optional<Category> getCategory = categoryService.existsById(createGameRequest.getCategory_id());

        if(getCategory.isEmpty()) {
            throw new IllegalStateException("Category does not exist.");
        }

        if (existsByTitle(createGameRequest.getTitle())) {
            throw new IllegalStateException("Game with this title already exists");
        }

        Game game = Game.builder()
                .title(createGameRequest.getTitle())
                .description(createGameRequest.getDescription())
                .storage(createGameRequest.getStorage())
                .price(createGameRequest.getPrice())
                .img_url(createGameRequest.getImg_url())
                .owner(user)
                .category(getCategory.get())
                .build();

        gameRepository.save(game);
    }


    public Boolean existsByTitle(String title) {
        return gameRepository.existsByTitle(title);
    }
}
