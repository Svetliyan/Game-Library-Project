package app.web;

import app.category.model.Category;
import app.exception.UsernameAlreadyExistException;
import app.game.model.Game;
import app.game.service.GameService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.servlet.ModelAndView;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(IndexController.class)
@ExtendWith(MockitoExtension.class)
public class IndexControllerApiTest {

    // ВАЖНО: Когато тествам контролери трябва да МОКНА всички депендънсита на този контролер с анотация @MockitoBean!!!
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private GameService gameService;

    @MockitoBean
    private AuthenticationDetails authenticationDetails;

    @MockitoBean
    private GameController gameController;

    // Използвам MockMvc за да изпращам заявки
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToIndexEndpoint_shouldReturnIndexView() throws Exception {

        MockHttpServletRequestBuilder request = get("/");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getRequestToRegisterEndpoint_shouldReturnRegisterView() throws Exception {

        // 1. Build Request
        MockHttpServletRequestBuilder request = get("/register");

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    void getRequestToLoginEndpoint_shouldReturnLoginView() throws Exception {

        MockHttpServletRequestBuilder request = get("/login");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest"));
    }

    @Test
    void getRequestToLoginEndpointWithErrorParameter_shouldReturnLoginViewAndErrorMessageAttribute() throws Exception {

        // 1. Build Request
        MockHttpServletRequestBuilder request = get("/login").param("error", "");

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest", "errorMessage"));
    }

    @Test
    void postRequestToRegisterEndpoint_happyPath() throws Exception {

        // 1. Build Request
        MockHttpServletRequestBuilder request = post("/register")
                .formField("username", "Vik123")
                .formField("password", "123456")
                .formField("email", "vik@abv.bg")
                .with(csrf());

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(userService, times(1)).registerUser(any());
    }

    @Test
    void postRequestToRegisterEndpointWithInvalidData_returnRegisterView() throws Exception {

        // 1. Build Request
        MockHttpServletRequestBuilder request = post("/register")
                .formField("username", "")
                .formField("email", "")
                .formField("password", "")
                .with(csrf());

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
        verify(userService, never()).registerUser(any());
    }

    @Test
    void getUnauthenticatedRequestToHome_redirectToLogin() throws Exception {

        // 1. Build Request
        MockHttpServletRequestBuilder request = get("/index");

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());
        verify(userService, never()).getById(any());
    }

    @Test
    void getStorePage_shouldReturnModelAndViewWithUserAndGames() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(userId);

        List<Game> mockGames = List.of(new Game(), new Game());

        AuthenticationDetails authDetails = new AuthenticationDetails(userId, "testUser", "password", UserRole.USER, true);

        when(userService.getById(userId)).thenReturn(mockUser);
        when(gameService.getAllGames()).thenReturn(mockGames);

        // Мокваме аутентикация и настройваме подходящи права
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authDetails, null, List.of(() -> "ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Act
        MvcResult result = mockMvc.perform(get("/store"))
                .andExpect(status().isOk())  // Очакваме статус 200 (успешен)
                .andExpect(view().name("store"))  // Очакваме да бъде върнат изглед с името "store"
                .andReturn();

        // Assert
        ModelAndView modelAndView = result.getModelAndView();

        assertEquals("store", modelAndView.getViewName());
        assertEquals(mockUser, modelAndView.getModel().get("user"));
        assertEquals(mockGames, modelAndView.getModel().get("allSystemGames"));
    }
}
