package app.user.service;

import app.user.model.User;
import app.user.repository.UserRepository;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import app.web.dto.UserDetailsRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail());

        if(optionalUser.isPresent()) {
            throw new RuntimeException("This username or email already exists.");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .balance(BigDecimal.ZERO)
                .build();

        userRepository.save(user);
    }

    public User loginUser(LoginRequest loginRequest) {

        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User with this username does not exist.");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new RuntimeException("Incorrect password.");
        }

        return user;
    }

    public User getById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with this id [%s] does not exist.".formatted(userId)));
    }

    public void updateDetails(UUID id, UserDetailsRequest userDetailsRequest) {
        User user = getById(id);

        if(userDetailsRequest.getUsername() != null){
            user.setUsername(userDetailsRequest.getUsername());
        }

        if(userDetailsRequest.getPassword() != null){
            user.setPassword(userDetailsRequest.getPassword());
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
}
