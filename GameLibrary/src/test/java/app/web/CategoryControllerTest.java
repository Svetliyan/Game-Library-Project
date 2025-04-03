package app.web;

import app.category.service.CategoryService;
import app.game.service.GameService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.CreateCategoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {
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
    private CategoryController categoryController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void testGetAddCategoryPage() throws Exception {
        UUID userId = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(userId);

        AuthenticationDetails authDetails = new AuthenticationDetails(userId, "testUser", "password", UserRole.USER, true);

        when(userService.getById(userId)).thenReturn(mockUser);

        // Act
        ModelAndView modelAndView = categoryController.getAddCategoryPage(authDetails);

        assertEquals("add-category", modelAndView.getViewName());
        assertEquals(mockUser, modelAndView.getModel().get("user"));
    }

    @Test
    void testCreateNewCategory_ValidData() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, "testUser", "password", UserRole.ADMIN, true);
        User mockUser = new User();
        mockUser.setId(userId);

        CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest();
        given(userService.getById(userId)).willReturn(mockUser);
        given(bindingResult.hasErrors()).willReturn(false);

        // When
        String viewName = categoryController.createNewCategory(createCategoryRequest, bindingResult, authenticationDetails);

        // Then
        assertEquals("redirect:/profile", viewName);
        verify(categoryService, times(1)).createCategory(createCategoryRequest, mockUser);
    }

    @Test
    void testCreateNewCategory_InvalidData() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, "testUser", "password", UserRole.ADMIN, true);
        User mockUser = new User();
        mockUser.setId(userId);

        CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest();
        given(userService.getById(userId)).willReturn(mockUser);
        given(bindingResult.hasErrors()).willReturn(true);

        // When
        String viewName = categoryController.createNewCategory(createCategoryRequest, bindingResult, authenticationDetails);

        // Then
        assertEquals("add-category", viewName);
        verify(categoryService, never()).createCategory(any(), any());
    }
}
