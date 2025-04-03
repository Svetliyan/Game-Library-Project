package app.user;

import static org.junit.jupiter.api.Assertions.*;  // Това осигурява assertFalse(), assertTrue(), assertThrows()
import static org.mockito.Mockito.*;
import app.exception.*;
import app.game.model.Game;
import app.game.service.GameService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import app.web.dto.UserDetailsRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private GameService gameService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerUser_ShouldSaveUser_WhenUsernameDoesNotExist() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("testUser", "test@example.com", "password123", BigDecimal.ZERO);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // When
        userService.registerUser(registerRequest);

        // Then
        verify(userRepository).save(any(User.class));
    }

    @Test
    void givenExistingUsername_whenRegisterUser_thenThrowException() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("test")
                .password("1234")
                .email("test@gmail.com")
                .build();
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        assertThrows(UsernameAlreadyExistException.class, () -> userService.registerUser(registerRequest));
    }

    @Test
    void givenMissingUserFromDatabase_whenEditUserDetails_thenExceptionIsThrown() {

        UUID userId = UUID.randomUUID();
        UserDetailsRequest dto = UserDetailsRequest.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> userService.updateDetails(userId, dto));
    }

    @Test
    void givenExistingUser_whenEditTheirProfileWithActualEmail_thenChangeTheirDetailsSaveNotificationPreferenceAndSaveToDatabase() {

        UUID userId = UUID.randomUUID();
        UserDetailsRequest dto = UserDetailsRequest.builder()
                .username("Boryana")
                .email("boryana99@gmail.com")
                .img_url("www.image.com")
                .build();

        User user = User.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateDetails(userId, dto);

        Assertions.assertEquals("Boryana", user.getUsername());
        Assertions.assertEquals("boryana99@gmail.com", user.getEmail());
        Assertions.assertEquals("www.image.com", user.getImg_url());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenExistingUsername_whenUpdateDetails_thenThrowTakenUsernameException() {

        UUID userId = UUID.randomUUID();
        UserDetailsRequest dto = UserDetailsRequest.builder()
                .username("Boryana")
                .email("boryana99@gmail.com")
                .img_url("www.image.com")
                .build();

        User user = User.builder().build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("Boryana")).thenReturn(true);

        assertThrows(TakenUsernameException.class, () -> userService.updateDetails(user.getId(), dto));
    }

    @Test
    void updateDetails_ShouldThrowUsernameLengthException_WhenUsernameIsTooShort() {

        UUID userId = UUID.randomUUID();
        UserDetailsRequest dto = UserDetailsRequest.builder()
                .username("aa")
                .email("boryana99@gmail.com")
                .img_url("www.image.com")
                .build();

        User user = User.builder().build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(UsernameLengthException.class, () -> userService.updateDetails(user.getId(), dto));
    }

    @Test
    void updateDetails_ShouldThrowUsernameLengthException_WhenUsernameIsTooLong() {

        UUID userId = UUID.randomUUID();
        UserDetailsRequest dto = UserDetailsRequest.builder()
                .username("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .email("boryana99@gmail.com")
                .img_url("www.image.com")
                .build();

        User user = User.builder().build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(UsernameLengthException.class, () -> userService.updateDetails(user.getId(), dto));
    }


    @Test
    void givenUserWithRoleAdmin_whenSwitchRole_thenUserReceivesUserRole() {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .role(UserRole.ADMIN)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchRole(userId);

        assertThat(user.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void givenUserWithRoleUser_whenSwitchRole_thenUserReceivesUserRole() {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .role(UserRole.USER)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchRole(userId);

        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void givenNonExistingUsername_whenLoadUserByUsername_thenThrowDomainException() {

        String username = "nonExistingUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> userService.loadUserByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void givenUsersInRepository_whenGetAllUsers_thenReturnUserList() {
        // Given
        List<User> users = List.of(
                User.builder()
                        .id(UUID.randomUUID())
                        .username("user1")
                        .email("user1@example.com")
                        .password("password")
                        .role(UserRole.USER)
                        .isActive(true)
                        .balance(BigDecimal.ZERO)
                        .build(),
                User.builder()
                        .id(UUID.randomUUID())
                        .username("user2")
                        .email("user2@example.com")
                        .password("password")
                        .role(UserRole.USER)
                        .isActive(true)
                        .balance(BigDecimal.ZERO)
                        .build()
        );
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users.size(), result.size());
        assertEquals(users, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void givenExistingUser_whenSwitchStatus_thenToggleActiveState() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("testUser")
                .email("test@example.com")
                .password("password123")
                .role(UserRole.USER)
                .isActive(true) // Начално състояние: активен
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.switchStatus(userId);

        // Then
        assertFalse(user.isActive()); // Проверява, че статусът е сменен на false
        verify(userRepository, times(1)).save(user); // Проверява, че save() е извикан веднъж

        // When (втори път да тестваме превключване)
        userService.switchStatus(userId);

        // Then
        assertTrue(user.isActive()); // Проверява, че статусът е сменен обратно на true
        verify(userRepository, times(2)).save(user); // save() трябва да е извикан два пъти общо
    }

    @Test
    void givenNonExistingUser_whenSwitchStatus_thenThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DomainException.class, () -> userService.switchStatus(userId));
        verify(userRepository, never()).save(any()); // Уверява се, че save() не е извикан
    }

    @Test
    void givenValidUser_whenSave_thenUserIsSaved() {
        // Given
        User user = User.builder()
                .username("testUser")
                .email("test@example.com")
                .password("password123")
                .build();

        // When
        userService.save(user);

        // Then
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenExistingUsername_whenLoadUserByUsername_thenReturnAuthenticationDetails() {
        // Given
        UUID userId = UUID.randomUUID();
        String username = "testUser";
        String password = "encodedPassword";
        UserRole role = UserRole.USER;

        User user = User.builder()
                .id(userId)
                .username(username)
                .password(password)
                .role(role)
                .isActive(true)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userService.loadUserByUsername(username);

        // Then
        assertNotNull(userDetails);
        assertTrue(userDetails instanceof AuthenticationDetails);
        AuthenticationDetails authDetails = (AuthenticationDetails) userDetails;

        assertEquals(userId, authDetails.getId());
        assertEquals(username, authDetails.getUsername());
        assertEquals(password, authDetails.getPassword());
        assertEquals(role, authDetails.getRole());
        assertTrue(authDetails.isEnabled());

        verify(userRepository, times(1)).findByUsername(username);
    }
}
