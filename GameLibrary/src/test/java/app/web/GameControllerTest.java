package app.web;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.game.model.Game;

import static org.mockito.Mockito.*;
import app.game.service.GameService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.CreateGameRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private GameService gameService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private AuthenticationDetails authenticationDetails;

    @InjectMocks
    private GameController gameController;

    private MockMvc mockMvc;

    @Test
    void getStorePage_shouldReturnModelAndViewWithUserAndGames() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(userId);

        List<Game> mockGames = List.of(new Game(), new Game());

        AuthenticationDetails authDetails = new AuthenticationDetails(userId, "testUser", "password", UserRole.USER, true);

        when(userService.getById(userId)).thenReturn(mockUser);
        when(gameService.getAllGames()).thenReturn(mockGames);

        // Act
        ModelAndView modelAndView = gameController.getStorePage(authDetails);

        // Assert
        assertEquals("store", modelAndView.getViewName());
        assertEquals(mockUser, modelAndView.getModel().get("user"));
        assertEquals(mockGames, modelAndView.getModel().get("allSystemGames"));
    }

    @Test
    void givenAuthenticatedUser_whenGetAddGamePage_thenReturnModelAndViewWithAttributes() {
        // Given
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, "testUser", "password", UserRole.USER, true);
        User mockUser = new User();
        mockUser.setId(userId);

        Category category1 = new Category();
        category1.setId(1);
        category1.setName("Action");

        Category category2 = new Category();
        category2.setId(2);
        category2.setName("RPG");

        List<Category> categories = List.of(category1, category2);

        given(userService.getById(userId)).willReturn(mockUser);
        given(categoryService.getAllCategories()).willReturn(categories);

        // When
        ModelAndView modelAndView = gameController.getAddGamePage(authenticationDetails);

        // Then
        assertEquals("add-game", modelAndView.getViewName());
        assertEquals(mockUser, modelAndView.getModel().get("user"));
        assertEquals(categories, modelAndView.getModel().get("categories"));
        assertNotNull(modelAndView.getModel().get("createGameRequest"));
    }

    @Test
    void givenValidGameRequest_whenCreateNewGame_thenRedirectToStore() {
        // Given
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, "testUser", "password", UserRole.USER, true);
        User mockUser = new User();
        mockUser.setId(userId);

        CreateGameRequest createGameRequest = new CreateGameRequest();
        given(userService.getById(userId)).willReturn(mockUser);
        given(bindingResult.hasErrors()).willReturn(false);

        // When
        String viewName = gameController.createNewGame(createGameRequest, bindingResult, authenticationDetails);

        // Then
        assertEquals("redirect:/store", viewName);
        verify(gameService, times(1)).createGame(createGameRequest, mockUser);
    }

    @Test
    void givenInvalidGameRequest_whenCreateNewGame_thenReturnAddGameView() {
        // Given
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, "testUser", "password", UserRole.USER, true);
        User mockUser = new User();
        mockUser.setId(userId);

        CreateGameRequest createGameRequest = new CreateGameRequest();

        given(userService.getById(userId)).willReturn(mockUser);
        given(bindingResult.hasErrors()).willReturn(true);

        // When
        String viewName = gameController.createNewGame(createGameRequest, bindingResult, authenticationDetails);

        // Then
        assertEquals("add-game", viewName);
        verify(gameService, never()).createGame(any(), any());
    }

    @Test
    void givenValidGame_whenGetGamePage_thenReturnGameView() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, "testUser", "password", UserRole.USER, true);
        User mockUser = new User();
        Game mockGame = new Game();

        given(userService.getById(userId)).willReturn(mockUser);
        given(gameService.getById(gameId)).willReturn(mockGame);

        // When
        ModelAndView modelAndView = gameController.getGamePage(gameId, authenticationDetails);

        // Then
        assertEquals("games", modelAndView.getViewName());
        assertEquals(mockUser, modelAndView.getModel().get("user"));
        assertEquals(mockGame, modelAndView.getModel().get("game"));

        verify(userService, times(1)).getById(userId);
        verify(gameService, times(1)).getById(gameId);
    }

    @Test
    void givenValidGameId_whenGetGamePage_thenReturnGameView() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, "testUser", "password", UserRole.USER, true);
        User mockUser = new User();
        Game mockGame = new Game();

        given(userService.getById(userId)).willReturn(mockUser);
        given(gameService.getById(gameId)).willReturn(mockGame);

        // When
        ModelAndView modelAndView = gameController.getGamePage(gameId, authenticationDetails);

        // Then
        assertEquals("games", modelAndView.getViewName());
        assertEquals(mockUser, modelAndView.getModel().get("user"));
        assertEquals(mockGame, modelAndView.getModel().get("game"));

        verify(userService).getById(userId);
        verify(gameService).getById(gameId);
    }

    @Test
    void purchaseGame_shouldRedirectToLibrary() {
        // Подготвяне на тестови данни
        UUID gameId = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());

        // Мокваме поведение на зависимостите
        when(authenticationDetails.getId()).thenReturn(mockUser.getId()); // Мокваме ID-то на потребителя
        when(userService.getById(any(UUID.class))).thenReturn(mockUser);
        doNothing().when(gameService).purchaseGame(any(UUID.class), any(User.class));

        // Извикване на метода директно
        String viewName = gameController.purchaseGame(gameId, authenticationDetails);

        // Проверка на резултата
        assertEquals("redirect:/games/library", viewName);

        // Проверка дали методите на зависимостите са извикани правилно
        verify(userService, times(1)).getById(any());
        verify(gameService, times(1)).purchaseGame(any(), any());
    }


    @Test
    void getGameById_shouldReturnGameAndUser() {
        // Подготвяне на тестови данни
        UUID gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Game mockGame = new Game();
        mockGame.setId(gameId);
        User mockUser = new User();
        mockUser.setId(userId);

        // Мокваме поведението на зависимостите
        when(gameService.getById(gameId)).thenReturn(mockGame);
        when(authenticationDetails.getId()).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(mockUser);

        // Извикваме метода
        ModelAndView modelAndView = gameController.getGameById(gameId, authenticationDetails);

        // Проверки
        assertEquals("games", modelAndView.getViewName()); // Проверяваме дали е правилното View
        assertNotNull(modelAndView.getModel().get("game")); // Проверка дали 'game' е добавен в модела
        assertNotNull(modelAndView.getModel().get("user")); // Проверка дали 'user' е добавен в модела
        assertEquals(mockGame, modelAndView.getModel().get("game")); // Проверяваме дали 'game' е същото като mockGame
        assertEquals(mockUser, modelAndView.getModel().get("user")); // Проверяваме дали 'user' е същото като mockUser
    }

    @Test
    void getLibraryPage_shouldReturnLibraryViewWithUser() {
        // Given
        UUID userId = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(userId);
        when(authenticationDetails.getId()).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(mockUser);

        // When
        ModelAndView modelAndView = gameController.getLibraryPage(authenticationDetails);

        // Then
        assertEquals("library", modelAndView.getViewName());
        assertNotNull(modelAndView.getModel().get("user"));
        assertEquals(mockUser, modelAndView.getModel().get("user"));
    }

    @Test
    void deleteGame_shouldRedirectToStore() {
        // Given
        UUID gameId = UUID.randomUUID();

        // When
        String result = gameController.deleteGame(gameId);

        // Then
        assertEquals("redirect:/store", result);
        verify(gameService, times(1)).deleteById(gameId);
    }

    @Test
    void updateGame_shouldReturnErrorPageWhenExceptionOccurs() {
        // Given
        UUID gameId = UUID.randomUUID();
        CreateGameRequest mockRequest = new CreateGameRequest();
        doThrow(new RuntimeException("Game update failed")).when(gameService).updateGame(gameId, mockRequest);

        // When
        String result = gameController.updateGame(gameId, mockRequest);

        // Then
        assertEquals("internal-server-error", result);
    }

    @Test
    void givenValidGameRequest_whenUpdateGame_thenRedirectToStore() {
        // Given
        UUID gameId = UUID.randomUUID();
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setTitle("Updated Game");

        // Mocking successful game update
        doNothing().when(gameService).updateGame(eq(gameId), eq(createGameRequest));

        // When
        String result = gameController.updateGame(gameId, createGameRequest);

        // Then
        assertEquals("redirect:/store", result);
        verify(gameService, times(1)).updateGame(gameId, createGameRequest);
    }

    @Test
    void givenErrorDuringUpdate_whenUpdateGame_thenReturnErrorPage() {
        // Given
        UUID gameId = UUID.randomUUID();
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setTitle("Faulty Game");

        // Mocking failure in game update
        doThrow(new RuntimeException("Game update failed")).when(gameService).updateGame(eq(gameId), eq(createGameRequest));

        // When
        String result = gameController.updateGame(gameId, createGameRequest);

        // Then
        assertEquals("internal-server-error", result);
    }

    @Test
    void testGetEditGamePage() throws Exception {
        // Arrange

        Category category = new Category();
        category.setId(1);  // ID е int
        category.setName("Action");

        UUID gameId = UUID.randomUUID();
        Game game = new Game();
        game.setId(gameId);
        game.setTitle("Test Game");
        game.setDescription("Test Description");
        game.setStorage(50);
        game.setPrice(new BigDecimal("19.99"));
        game.setCategory(category);
        game.setCoverImg_url("testCover.jpg");
        game.setMainImg_url("testMain.jpg");
        game.setFirstImage_url("first.jpg");
        game.setSecondImage_url("second.jpg");
        game.setThirdImage_url("third.jpg");
        game.setFourthImage_url("fourth.jpg");

        // Mocking the services
        when(gameService.getById(gameId)).thenReturn(game);
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(category));

        // Act
        ModelAndView modelAndView = gameController.getEditGamePage(gameId);

        // Assert
        assertNotNull(modelAndView);
        assertEquals("edit-game", modelAndView.getViewName());
        assertNotNull(modelAndView.getModel().get("game"));
        assertNotNull(modelAndView.getModel().get("createGameRequest"));
        assertNotNull(modelAndView.getModel().get("categories"));

        // Checking if the game details are correctly added to the model
        CreateGameRequest createGameRequest = (CreateGameRequest) modelAndView.getModel().get("createGameRequest");
        assertEquals("Test Game", createGameRequest.getTitle());
        assertEquals("Test Description", createGameRequest.getDescription());
        assertEquals(50, createGameRequest.getStorage());
        assertEquals(new BigDecimal("19.99"), createGameRequest.getPrice());
        assertEquals("testCover.jpg", createGameRequest.getCoverImage_url());
        assertEquals("testMain.jpg", createGameRequest.getMainImg_url());
        assertEquals("first.jpg", createGameRequest.getFirstImage_url());
        assertEquals("second.jpg", createGameRequest.getSecondImage_url());
        assertEquals("third.jpg", createGameRequest.getThirdImage_url());
        assertEquals("fourth.jpg", createGameRequest.getFourthImage_url());
        assertEquals(category.getId(), createGameRequest.getCategory_id());
    }
}
