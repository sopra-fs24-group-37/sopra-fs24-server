package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getUsers_successful() {
        User user1 = new User();
        user1.setUsername("testUser1");
        User user2 = new User();
        user2.setUsername("testUser2");
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getUsers();
        assertEquals(2, users.size(), "Should return 2 users");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getUsers_emptyList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        List<User> users = userService.getUsers();
        assertTrue(users.isEmpty(), "Should return an empty list");
    }

    @Test
    public void findUserById_found() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUser");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.findUserbyId(1L);
        assertNotNull(foundUser, "User should be found");
        assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    public void findUserById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.findUserbyId(1L), "Should throw a ResponseStatusException when user is not found");
    }

    @Test
    public void createUser_validUser() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("password");

        when(userRepository.findByUsername("newUser")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User createdUser = userService.createUser(newUser);
        assertNotNull(createdUser.getToken(), "Token should not be null");
        assertEquals(UserStatus.ONLINE, createdUser.getStatus(), "User status should be OFFLINE");
    }

    @Test
    public void createUser_duplicateUsername() {
        User existingUser = new User();
        existingUser.setUsername("newUser");
        existingUser.setPassword("password");

        when(userRepository.findByUsername("newUser")).thenReturn(existingUser);

        assertThrows(ResponseStatusException.class, () -> userService.createUser(existingUser), "Should throw a conflict when username is taken");
    }

    @Test
    public void loginUser_successful() {
        User existingUser = new User();
        existingUser.setUsername("testUser");
        existingUser.setPassword("password");

        when(userRepository.findByUsername("testUser")).thenReturn(existingUser);

        User loggedInUser = userService.loginUser(existingUser);
        assertEquals(UserStatus.ONLINE, loggedInUser.getStatus(), "User status should be set to ONLINE after login");
    }

    @Test
    public void loginUser_failed() {
        User loginAttempt = new User();
        loginAttempt.setUsername("testUser");
        loginAttempt.setPassword("wrongPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> userService.loginUser(loginAttempt), "Should throw an exception for wrong credentials");
    }
}
