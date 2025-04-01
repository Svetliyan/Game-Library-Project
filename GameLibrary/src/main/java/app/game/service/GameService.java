package app.game.service;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.exception.*;
import app.game.model.Game;
import app.game.model.PurchasedGame;
import app.game.repository.GameRepository;
import app.game.repository.PurchasedGameRepository;
import app.user.model.User;
import app.user.repository.UserRepository;
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

    private final UserRepository userRepository;

    @Autowired
    public GameService(GameRepository gameRepository, CategoryService categoryService,
                       PurchasedGameRepository purchasedGameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.categoryService = categoryService;
        this.purchasedGameRepository = purchasedGameRepository;
        this.userRepository = userRepository;
    }

    public void createGame(CreateGameRequest createGameRequest, User user) {
        Optional<Category> getCategory = categoryService.existsById(createGameRequest.getCategory_id());

        if (existsByTitle(createGameRequest.getTitle())) {
            throw new GameAlreadyExistException("This game already exists!");
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

        PurchasedGame purchasedGame = new PurchasedGame();
        purchasedGame.setGame(game); // Свързваме с оригиналната игра
        purchasedGame.setOwner(user);

        if (user.getBalance().compareTo(game.getPrice()) < 0) {
            throw new NotEnoughMoney("Not enough balance to buy this game.");
        } else {
            user.setBalance(user.getBalance().subtract(game.getPrice()));
        }

        purchasedGameRepository.save(purchasedGame);
    }

    public boolean isGamePurchased(UUID gameId, User user) {
        return user.getPurchasedGames()
                .stream()
                .anyMatch(pg -> pg.getGame().getId().equals(gameId));
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
        Game game = getById(gameId);

        if (createGameRequest.getTitle() != null) {
            if (existsByTitle(createGameRequest.getTitle())) {
                throw new TakenTitleException("This game already exists!");
            }

            game.setTitle(createGameRequest.getTitle());
        }

        if (createGameRequest.getDescription() != null) {
            game.setDescription(createGameRequest.getDescription());
        }

        if (createGameRequest.getStorage() != 0) {
            game.setStorage(createGameRequest.getStorage());
        }

        if (createGameRequest.getPrice() != null) {
            game.setPrice(createGameRequest.getPrice());
        }

        if (createGameRequest.getCoverImage_url() != null) {
            game.setCoverImg_url(createGameRequest.getCoverImage_url());
        }

        if (createGameRequest.getMainImg_url() != null) {
            game.setMainImg_url(createGameRequest.getMainImg_url());
        }

        if (createGameRequest.getFirstImage_url() != null) {
            game.setFirstImage_url(createGameRequest.getFirstImage_url());
        }

        if (createGameRequest.getSecondImage_url() != null) {
            game.setSecondImage_url(createGameRequest.getSecondImage_url());
        }

        if (createGameRequest.getThirdImage_url() != null) {
            game.setThirdImage_url(createGameRequest.getThirdImage_url());
        }

        if (createGameRequest.getFourthImage_url() != null) {
            game.setFourthImage_url(createGameRequest.getFourthImage_url());
        }

        if (createGameRequest.getCategory_id() != null) {
            Category category = categoryService.getById(createGameRequest.getCategory_id());
            game.setCategory(category);
        }

        gameRepository.save(game);
    }

}
