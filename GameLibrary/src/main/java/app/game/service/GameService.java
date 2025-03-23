package app.game.service;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.game.model.Game;
import app.game.model.PurchasedGame;
import app.game.repository.GameRepository;
import app.game.repository.PurchasedGameRepository;
import app.user.model.User;
import app.web.dto.CreateGameRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final CategoryService categoryService;
    private final PurchasedGameRepository purchasedGameRepository;

    @Autowired
    public GameService(GameRepository gameRepository, CategoryService categoryService,
                       PurchasedGameRepository purchasedGameRepository) {
        this.gameRepository = gameRepository;
        this.categoryService = categoryService;
        this.purchasedGameRepository = purchasedGameRepository;
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
                .createdOn(LocalDateTime.now())
                .owner(user)
                .isVisible(true)
                .category(getCategory.get())
                .build();

        gameRepository.save(game);
    }

    public void purchaseGame(UUID gameId, User user) {

        Game game = getById(gameId);

        boolean isPurchased = user.getPurchasedGames()
                .stream()
                .anyMatch(pg -> pg.getGame().getId().equals(game.getId())); // Проверка по ID

        if (isPurchased) {
            return; // Вече притежава играта
        }

        PurchasedGame purchasedGame = new PurchasedGame();
        purchasedGame.setGame(game); // Свързваме с оригиналната игра
        purchasedGame.setOwner(user);

        purchasedGameRepository.save(purchasedGame);
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

    @Transactional
    public void deleteById(UUID id) {
        gameRepository.deleteById(id);
    }

    public void updateGame(UUID gameId, CreateGameRequest createGameRequest) {
        Game game = getById(gameId); // Взимаме съществуващата игра

        game.setTitle(createGameRequest.getTitle());
        game.setDescription(createGameRequest.getDescription());
        game.setStorage(createGameRequest.getStorage());
        game.setPrice(createGameRequest.getPrice());
        game.setCoverImg_url(createGameRequest.getCoverImage_url());
        game.setMainImg_url(createGameRequest.getMainImg_url());
        game.setFirstImage_url(createGameRequest.getFirstImage_url());
        game.setSecondImage_url(createGameRequest.getSecondImage_url());
        game.setThirdImage_url(createGameRequest.getThirdImage_url());
        game.setFourthImage_url(createGameRequest.getFourthImage_url());

        Category category = categoryService.getById(createGameRequest.getCategory_id());
        game.setCategory(category);

        gameRepository.save(game);
    }
}
