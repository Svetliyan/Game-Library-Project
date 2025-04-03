package app;

import app.category.model.Category;
import app.category.repository.CategoryRepository;
import app.category.service.CategoryService;
import app.user.model.User;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.CreateCategoryRequest;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest // Integration Test
public class CategoryITest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Test
    void createCategory(){
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Test")
                .email("svetliyan_s@abv.bg")
                .password("1234")
                .build();
        User registeredUser = userService.registerUser(registerRequest);

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
                .name("New Category")
                .build();

        // When
        categoryService.createCategory(createCategoryRequest, registeredUser);

        // Then
        Optional<Category> category = categoryRepository.findByName("New Category");
        assertTrue(category.isPresent());
        assertEquals("New Category", category.get().getName());
    }
}
