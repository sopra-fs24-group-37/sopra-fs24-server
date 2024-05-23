package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class LobbyStompControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private GameService gameService;

    @Mock
    private WebSocketService webSocketService;

    @InjectMocks
    private LobbyStompController lobbyStompController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void updateUsers_success() {
        // Arrange
        User user1 = new User();
        User user2 = new User();
        List<User> users = Arrays.asList(user1, user2);

        when(userService.getUsers()).thenReturn(users);

        // Act
        lobbyStompController.updateUsers();

        // Assert
        verify(userService, times(1)).getUsers();
        verify(webSocketService, times(1)).sendMessageToSubscribers(eq("/topic/users/getUsers"), anyList());
    }

    @Test
    public void updateGames_success() {
        // Arrange
        Game game1 = new Game();
        Game game2 = new Game();
        List<Game> games = Arrays.asList(game1, game2);

        when(gameService.getGames()).thenReturn(games);

        // Act
        lobbyStompController.updateGames();

        // Assert
        verify(gameService, times(1)).getGames();
        verify(webSocketService, times(1)).sendMessageToSubscribers(eq("/topic/games/getGames"), anyList());
    }
}