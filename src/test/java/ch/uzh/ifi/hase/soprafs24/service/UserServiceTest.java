package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    public void deleteUserByIdUserExists() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setUserId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        userService.deleteUserById(userId);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    public void deleteUserByIdUserNotFound() {
        // Arrange
        Long userId = 999L; // Assuming 999 is an ID that does not exist
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.deleteUserById(userId),
            "User with user id " + userId + " was not found!");

        verify(userRepository, never()).deleteById(any()); // Ensure deleteById is never called
    }

    @Test
    public void testSetUserOfflineUserExists() {
        // Arrange
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setStatus(UserStatus.ONLINE);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        userService.setUserOffline(userId);

        // Assert
        verify(userRepository).findById(userId);
        assertEquals(UserStatus.OFFLINE, mockUser.getStatus());
        verify(userRepository).save(mockUser);
    }

    @Test
    public void testSetUserOfflineUserNotFound() {
        // Arrange
        Long userId = 999L; // Assuming 999 is an ID that does not exist
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.setUserOffline(userId));
    }

    @Test
    public void testUpdateUserUserExists() {
        // Arrange
        User existingUser = new User();
        existingUser.setUserId(1L);
        existingUser.setUsername("Existing User");
        when(userRepository.findById(existingUser.getUserId())).thenReturn(Optional.of(existingUser));

        // Act
        User returnedUser = userService.updateUser(existingUser);

        // Assert
        verify(userRepository).findById(existingUser.getUserId());
        verify(userRepository).save(existingUser);
        assertEquals(existingUser, returnedUser);
    }

    @Test
    public void testUpdateUserUserNotFound() {
        // Arrange
        User newUser = new User();
        newUser.setUserId(2L);
        newUser.setUsername("New User");
        when(userRepository.findById(newUser.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.updateUser(newUser));
    }

}
