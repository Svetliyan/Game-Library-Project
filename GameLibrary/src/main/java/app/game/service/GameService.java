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

import java.util.List;
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
                .coverImg_url(createGameRequest.getCoverImage_url())
                .mainImg_url(createGameRequest.getMainImg_url())
                .firstImage_url(createGameRequest.getFirstImage_url())
                .secondImage_url(createGameRequest.getSecondImage_url())
                .thirdImage_url(createGameRequest.getThirdImage_url())
                .fourthImage_url(createGameRequest.getFourthImage_url())
                .owner(user)
                .isVisible(true)
                .category(getCategory.get())
                .build();

        gameRepository.save(game);
    }


    public Boolean existsByTitle(String title) {
        return gameRepository.existsByTitle(title);
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Game getById(UUID gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> new RuntimeException("Game with this id [%s] does not exist.".formatted(gameId)));
    }
}
