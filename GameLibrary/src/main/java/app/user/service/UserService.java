package app.user.service;

import app.exception.TakenUsernameException;
import app.exception.UsernameAlreadyExistException;
import app.exception.UsernameLengthException;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.web.dto.RegisterRequest;
import app.web.dto.UserDetailsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        Optional<User> optionUser = userRepository.findByUsername(registerRequest.getUsername());

        if (optionUser.isPresent()) {
            throw new UsernameAlreadyExistException("Username [%s] already exist.".formatted(registerRequest.getUsername()));
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .balance(BigDecimal.ZERO)
                .role(UserRole.USER)
                .isActive(true)
                .build();

        userRepository.save(user);
    }

    public User getById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with this id [%s] does not exist.".formatted(userId)));
    }

    public void updateDetails(UUID id, UserDetailsRequest userDetailsRequest) {
        User user = getById(id);

        if(userDetailsRequest.getUsername() != null){
            if (userRepository.existsByUsername(userDetailsRequest.getUsername())) {
                throw new TakenUsernameException("Username already exists.");
            }

            if (userDetailsRequest.getUsername().length() < 3 || userDetailsRequest.getUsername().length() > 40) {
                throw new UsernameLengthException("Username must be between 3 and 40 characters!");
            }

            user.setUsername(userDetailsRequest.getUsername());
        }

        if(userDetailsRequest.getEmail() != null){
            user.setEmail(userDetailsRequest.getEmail());
        }

        if(userDetailsRequest.getImg_url() != null){
            user.setImg_url(userDetailsRequest.getImg_url());
        }

        userRepository.save(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User with this username does not exist."));

        return new AuthenticationDetails(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }

    @Cacheable("users")
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    @CacheEvict(value = "users", allEntries = true)
    public void switchStatus(UUID userId) {

        User user = getById(userId);

        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void switchRole(UUID userId) {

        User user = getById(userId);

        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }

        userRepository.save(user);
    }
}
