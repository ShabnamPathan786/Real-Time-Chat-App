package com.shabnam.realtime_chat_app.service;

import com.shabnam.realtime_chat_app.config.JwtTokenProvider;
import com.shabnam.realtime_chat_app.model.User;
import com.shabnam.realtime_chat_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .username("testuser")
                .password("hashed_password")
                .email("test@example.com")
                .build();
    }

    @Test
    void register_ShouldSaveUser_WhenUsernameIsAvailable() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("raw_password")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User registered = userService.register("testuser", "raw_password", "test@example.com");

        assertNotNull(registered);
        assertEquals("testuser", registered.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> 
            userService.register("testuser", "raw_password", "test@example.com")
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("raw_password", "hashed_password")).thenReturn(true);
        when(jwtTokenProvider.generateToken("testuser")).thenReturn("mocked_jwt_token");

        String token = userService.login("testuser", "raw_password");

        assertEquals("mocked_jwt_token", token);
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            userService.login("unknown", "password")
        );
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsIncorrect() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrong_password", "hashed_password")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> 
            userService.login("testuser", "wrong_password")
        );
    }
}
