package ch.uzh.ifi.hase.soprafs24.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllUsers_successEmpty() {
        // Arrange
        List<User> users = new ArrayList<>();
        when(userService.getUsers()).thenReturn(users);

        // Act
        List<UserGetDTO> userGetDTOs = userController.getAllUsers();

        // Assert
        assertNotNull(userGetDTOs);
        assertEquals(0, userGetDTOs.size());
    }

    @Test
    public void getAllUsers_success() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        when(userService.findUserbyId(userId)).thenReturn(user);

        Long userId2 = 2L;
        User user2 = new User();
        when(userService.findUserbyId(userId2)).thenReturn(user2);

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);
        when(userService.getUsers()).thenReturn(users);

        // Act
        List<UserGetDTO> userGetDTOs = userController.getAllUsers();

        // Assert
        assertNotNull(userGetDTOs);
        assertEquals(2, userGetDTOs.size());
    }

    @Test
    public void getUserById_success() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        when(userService.findUserbyId(userId)).thenReturn(user);

        // Act
        UserGetDTO userGetDTO = userController.getUserById(userId);

        // Assert
        assertNotNull(userGetDTO);
        // Add assertions for DTO content if needed
    }

    @Test
    public void deleteUser_success() {
        // Arrange
        Long userId = 1L;

        // Act
        ResponseEntity<?> response = userController.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Deleted", response.getBody());
        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    public void createUser_success() {
        // Arrange
        UserPostDTO userPostDTO = new UserPostDTO();
        User createdUser = new User();
        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        // Act
        ResponseEntity<?> response = userController.createUser(userPostDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        // Add assertions for body content if needed
    }

    @Test
    public void checkUser_success() {
        // Arrange
        UserPostDTO userPostDTO = new UserPostDTO();
        User user = new User();
        when(userService.loginUser(any(User.class))).thenReturn(user);

        // Act
        ResponseEntity<?> response = userController.checkUser(userPostDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Add assertions for body content if needed
    }

    @Test
    public void logoutUser_success() {
        // Arrange
        Long userId = 1L;

        // Act
        ResponseEntity<Void> response = userController.logoutUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).setUserOffline(userId);
    }

    @Test
    public void updateUser_success() {
        // Arrange
        Long userId = 1L;
        UserPutDTO userPutDTO = new UserPutDTO();
        User user = new User();
        when(userService.findUserbyId(userId)).thenReturn(user);
        when(userService.updateUser(any(User.class))).thenReturn(user);

        // Act
        UserGetDTO userGetDTO = userController.updateUser(userId, userPutDTO);

        // Assert
        assertNotNull(userGetDTO);
        // Add assertions for DTO content if needed
    }
}