package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        // Ensure the repository is clean before each test
        userRepository.deleteAll();
    }

    @Test
    public void testCreateUser_Success() {
        // Given
        User newUser = new User();
        newUser.setUsername("testUser");
        newUser.setPassword("testPassword");

        // When
        User createdUser = userService.createUser(newUser);

        // Then
        assertNotNull(createdUser.getUserId());
        assertEquals("testUser", createdUser.getUsername());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
        assertNotNull(createdUser.getToken());
        assertEquals(0, createdUser.getGamesPlayed());
        assertEquals(0, createdUser.getGamesWon());
        assertEquals(0, createdUser.getTotalScores());
    }

    @Test
    public void testCreateUser_Failure_DuplicateUsername() {
        // Given
        User firstUser = new User();
        firstUser.setUsername("duplicateUser");
        firstUser.setPassword("password");
        userService.createUser(firstUser);

        // When
        User secondUser = new User();
        secondUser.setUsername("duplicateUser");
        secondUser.setPassword("newPassword");

        // Then
        assertThrows(ResponseStatusException.class, () -> userService.createUser(secondUser));
    }

    @Test
    public void testFindUserById_UserExists() {
        // Given
        User newUser = new User();
        newUser.setUsername("findUser");
        newUser = userRepository.save(newUser);

        // When
        User foundUser = userService.findUserbyId(newUser.getUserId());

        // Then
        assertNotNull(foundUser);
        assertEquals(newUser.getUserId(), foundUser.getUserId());
    }

    @Test
    public void testFindUserById_UserDoesNotExists() {
        // Then
        assertThrows(ResponseStatusException.class, () -> userService.findUserbyId(999L));
    }

    @Test
    public void testLoginUser_Success() {
        // Given
        User newUser = new User();
        newUser.setUsername("loginUser");
        newUser.setPassword("password");
        userService.createUser(newUser);

        // When
        User loggedInUser = userService.loginUser(newUser);

        // Then
        assertNotNull(loggedInUser);
        assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
    }

    @Test
    public void testLoginUser_Failure_WrongPassword() {
        // Given
        User newUser = new User();
        newUser.setUsername("loginUser");
        newUser.setPassword("password");
        userService.createUser(newUser);

        newUser.setPassword("wrongPassword");

        // Then
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(newUser));
    }
}
