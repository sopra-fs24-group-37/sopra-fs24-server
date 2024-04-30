package ch.uzh.ifi.hase.soprafs24.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;

import java.util.List;
import java.util.ArrayList;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    // @Test
    // public void getAllUsersReturnsNotEmptyList() {
    //     User user = new User();
    //     user.setUserId(1L);
    //     user.setUsername("testUser");
    //     user.setToken("token123");
    //     user.setPassword("password");
    //     user.setStatus(UserStatus.ONLINE);
    //     user.setGamesPlayed(5);
    //     user.setGamesWon(2);
    //     user.setTotalScores(100);

    //     List<User> users = List.of(user);
    //     when(userService.getUsers()).thenReturn(users);
    //     when(DTOMapper.INSTANCE.convertEntityToUserGetDTO(any())).thenCallRealMethod();

    //     List<UserGetDTO> results = userController.getAllUsers();
    //     assertFalse(results.isEmpty(), "Expected non-empty list of users");
    //     assertEquals(1, results.size(), "Expected list size of 1");
    // }

    @Test
    public void getAllUsersReturnsEmptyList() {
        when(userService.getUsers()).thenReturn(new ArrayList<>());
        List<UserGetDTO> results = userController.getAllUsers();
        assertTrue(results.isEmpty(), "Expected empty list of users");
    }

    // @Test
    // public void getUserByIdFound() {
    //     User user = new User();
    //     user.setUserId(1L);
    //     user.setUsername("testUser");
    //     user.setToken("token123");
    //     user.setPassword("password");
    //     user.setStatus(UserStatus.ONLINE);
    //     user.setGamesPlayed(0);
    //     user.setGamesWon(0);
    //     user.setTotalScores(0);
    
    //     when(userService.findUserbyId(1L)).thenReturn(user);
    //     when(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user)).thenCallRealMethod();
    
    //     UserGetDTO result = userController.getUserById(1L);
    //     assertNotNull(result, "User should be found");
    //     assertEquals("testUser", result.getUsername(), "Expected username to match");
    // }

    @Test
    public void getUserByIdNotFound() {
        when(userService.findUserbyId(1L)).thenReturn(null);
        UserGetDTO result = userController.getUserById(1L);
        assertNull(result, "User should not be found");
    }

    // @Test
    // public void createUserSuccessful() {
    //     UserPostDTO userPostDTO = new UserPostDTO();
    //     userPostDTO.setUsername("newUser");
    //     userPostDTO.setPassword("newPassword");

    //     User newUser = new User();
    //     newUser.setUsername(userPostDTO.getUsername());
    //     newUser.setPassword(userPostDTO.getPassword());

    //     when(userService.createUser(any())).thenReturn(newUser);
    //     when(DTOMapper.INSTANCE.convertEntityToUserGetDTO(newUser)).thenCallRealMethod();

    //     ResponseEntity<?> response = userController.createUser(userPostDTO);
    //     assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Expected HTTP status 201");
    // }

    // @Test
    // public void createUserFailure() {
    //     UserPostDTO userPostDTO = new UserPostDTO();
    //     userPostDTO.setUsername("newUser");
    //     userPostDTO.setPassword("newPassword");
    //     userController.createUser(userPostDTO);

    //     when(userService.createUser(any())).thenReturn(null);

    //     ResponseEntity<?> response = userController.createUser(userPostDTO);
    //     assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Username is already taken!");
    // }

    // @Test
    // public void checkUserLoginSuccess() {
    //     UserPostDTO userPostDTO = new UserPostDTO();
    //     userPostDTO.setUsername("existUser");
    //     userPostDTO.setPassword("existPassword");

    //     User existUser = new User();
    //     existUser.setUsername(userPostDTO.getUsername());
    //     existUser.setPassword(userPostDTO.getPassword());

    //     when(userService.loginUser(any())).thenReturn(existUser);
    //     when(DTOMapper.INSTANCE.convertEntityToUserGetDTO(existUser)).thenCallRealMethod();

    //     UserGetDTO result = userController.checkUser(userPostDTO);
    //     assertNotNull(result, "Login should succeed");
    // }

}