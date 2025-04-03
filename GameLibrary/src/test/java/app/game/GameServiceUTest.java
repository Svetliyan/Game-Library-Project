package app.game;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.exception.GameAlreadyExistException;
import app.exception.NotEnoughMoney;
import app.game.model.Game;
import app.game.model.PurchasedGame;
import app.game.repository.GameRepository;
import app.game.repository.PurchasedGameRepository;
import app.game.service.GameService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateGameRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceUTest {
    @Mock
    private GameRepository gameRepository;

    @Mock
    private PurchasedGameRepository purchasedGameRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private GameService gameService;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<Game> gameCaptor;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void existsByTitle_ShouldReturnTrue_WhenGameExists() {
        // Given
        String gameTitle = "Test Game";
        when(gameRepository.existsByTitle(gameTitle)).thenReturn(true);

        // When
        boolean result = gameService.existsByTitle(gameTitle);

        // Then
        assertTrue(result);
        verify(gameRepository).existsByTitle(gameTitle);
    }

    @Test
    void existsByTitle_ShouldReturnFalse_WhenGameDoesNotExist() {
        // Given
        String gameTitle = "Nonexistent Game";
        when(gameRepository.existsByTitle(gameTitle)).thenReturn(false);

        // When
        boolean result = gameService.existsByTitle(gameTitle);

        // Then
        assertFalse(result);
        verify(gameRepository).existsByTitle(gameTitle);
    }

    @Test
    void getAllGames_ShouldReturnListOfGames() {
        // Given
        Game game1 = new Game();
        game1.setTitle("Game 1");

        Game game2 = new Game();
        game2.setTitle("Game 2");

        when(gameRepository.findAll()).thenReturn(List.of(game1, game2));

        // When
        List<Game> games = gameService.getAllGames();

        // Then
        assertEquals(2, games.size());
        assertEquals("Game 1", games.get(0).getTitle());
        assertEquals("Game 2", games.get(1).getTitle());
        verify(gameRepository).findAll();
    }

    @Test
    void getById_ShouldReturnGame_WhenGameExists() {
        // Given
        UUID gameId = UUID.randomUUID();
        Game game = new Game();
        game.setId(gameId);
        game.setTitle("Existing Game");

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        // When
        Game foundGame = gameService.getById(gameId);

        // Then
        assertNotNull(foundGame);
        assertEquals(gameId, foundGame.getId());
        assertEquals("Existing Game", foundGame.getTitle());
        verify(gameRepository).findById(gameId);
    }

    @Test
    void getById_ShouldThrowException_WhenGameDoesNotExist() {
        // Given
        UUID gameId = UUID.randomUUID();
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> gameService.getById(gameId));
        assertEquals("Game with this id [%s] does not exist.".formatted(gameId), exception.getMessage());
        verify(gameRepository).findById(gameId);
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete_WhenGameExists() {
        // Given
        UUID gameId = UUID.randomUUID();

        // When
        gameService.deleteById(gameId);

        // Then
        verify(gameRepository).deleteById(gameId);
    }

//    @Test
//    void updateGame_ShouldUpdateGame_WhenValidRequestProvided() {
//        // Given
//        UUID gameId = UUID.randomUUID();
//
//        Game existingGame = new Game();
//        existingGame.setId(gameId);
//        existingGame.setTitle("Old Title"); // СТАРО заглавие
//        existingGame.setDescription("Old Description");
//        existingGame.setStorage(30);
//        existingGame.setPrice(new BigDecimal("29.99"));
//
//        CreateGameRequest updateRequest = new CreateGameRequest(
//                "New Title",  // НОВО заглавие
//                "New Description",
//                50,
//                new BigDecimal("49.99"),
//                "newCover.jpg",
//                "newMain.jpg",
//                "newFirst.jpg",
//                "newSecond.jpg",
//                "newThird.jpg",
//                "newFourth.jpg"
//        );
//
//        when(gameRepository.findById(gameId)).thenReturn(Optional.of(existingGame));
//
//        // When
//        gameService.updateGame(gameId, updateRequest);
//
//        gameService.updateGame(gameId, updateRequest);
//
//        // Then
//        verify(gameRepository).save(gameCaptor.capture());
//
//        System.out.println("Before update: " + existingGame.getTitle()); // Очакваш "Old Title"
//        gameService.updateGame(gameId, updateRequest);
//        System.out.println("After update: " + updateRequest.getTitle());  // Очакваш "New Title"
//
//        Game savedGame = gameCaptor.getValue();
//        assertEquals("New Title", savedGame.getTitle());  // ✅ Очакваме новото заглавие
//        assertEquals("New Description", savedGame.getDescription());
//        assertEquals(50, savedGame.getStorage());
//        assertEquals(new BigDecimal("49.99"), savedGame.getPrice());
//        assertEquals("newCover.jpg", savedGame.getCoverImg_url());
//        assertEquals("newMain.jpg", savedGame.getMainImg_url());
//        assertEquals("newFirst.jpg", savedGame.getFirstImage_url());
//        assertEquals("newSecond.jpg", savedGame.getSecondImage_url());
//        assertEquals("newThird.jpg", savedGame.getThirdImage_url());
//        assertEquals("newFourth.jpg", savedGame.getFourthImage_url());
//
//    }

    @Test
    void givenExistingGame_whenUpdateGameDetails_thenChangeGameDetailsAndSaveToDatabase() {
        // Arrange
        UUID gameId = UUID.randomUUID();
        CreateGameRequest dto = CreateGameRequest.builder()
                .title("New Title")
                .description("New Description")
                .coverImage_url("New CoverImg")
                .mainImg_url("New MainImg")
                .firstImage_url("New FirstImg")
                .secondImage_url("New SecondImg")
                .thirdImage_url("New ThirdImg")
                .fourthImage_url("New FourthImg")
                .price(new BigDecimal("59.99"))
                .build();

        Game game = Game.builder().title("Old Title").price(new BigDecimal("19.99"))
                .description("Old Description")
                .coverImg_url("Old CoverImg")
                .mainImg_url("Old MainImg")
                .firstImage_url("Old FirstImg")
                .secondImage_url("Old SecondImg")
                .thirdImage_url("Old ThirdImg")
                .price(new BigDecimal("29.99"))
                .fourthImage_url("Old FourthImg").build();
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        // Act
        gameService.updateGame(gameId, dto);

        // Assert
        Assertions.assertEquals("New Title", game.getTitle());
        Assertions.assertEquals("New Description", game.getDescription());
        Assertions.assertEquals("New CoverImg", game.getCoverImg_url());
        Assertions.assertEquals("New MainImg", game.getMainImg_url());
        Assertions.assertEquals("New FirstImg", game.getFirstImage_url());
        Assertions.assertEquals("New SecondImg", game.getSecondImage_url());
        Assertions.assertEquals("New ThirdImg", game.getThirdImage_url());
        Assertions.assertEquals("New FourthImg", game.getFourthImage_url());
        Assertions.assertEquals(new BigDecimal("59.99"), game.getPrice());

        verify(gameRepository, times(1)).save(game);
    }

    @Test
    void givenPurchasedGame_whenCheckIfGameIsPurchased_thenReturnTrue() {
        // Arrange
        UUID gameId = UUID.randomUUID();
        User user = User.builder()
                .purchasedGames(Arrays.asList(
                        PurchasedGame.builder()
                                .game(Game.builder().id(gameId).build())
                                .build()
                ))
                .build();

        // Act
        boolean result = gameService.isGamePurchased(gameId, user);

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    void givenNonPurchasedGame_whenCheckIfGameIsPurchased_thenReturnFalse() {
        // Arrange
        UUID gameId = UUID.randomUUID();
        User user = User.builder()
                .purchasedGames(Collections.emptyList())
                .build();

        // Act
        boolean result = gameService.isGamePurchased(gameId, user);

        // Assert
        Assertions.assertFalse(result);
    }

}
