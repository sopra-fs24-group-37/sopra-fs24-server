package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GamePlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class GamePlayerServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GamePlayerRepository gamePlayerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private GameService gameService;

    @InjectMocks
    private GamePlayerService gamePlayerService;

    private UUID gameId;
    private User gameMaster;
    private User otherUser;
    private GamePlayer gamePlayer;
    private Game game;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        gameId = UUID.randomUUID();

        gameMaster = new User();
        gameMaster.setUserId(1L);
        gameMaster.setUsername("testUser");
        gameMaster.setToken("token123");
        gameMaster.setPassword("password");
        gameMaster.setStatus(UserStatus.ONLINE);
        gameMaster.setGamesPlayed(0);
        gameMaster.setGamesWon(0);
        gameMaster.setTotalScores(0);

        otherUser = new User();
        otherUser.setUserId(2L);
        otherUser.setUsername("testUser2");
        otherUser.setToken("token1234");
        otherUser.setPassword("password");
        otherUser.setStatus(UserStatus.ONLINE);
        otherUser.setGamesPlayed(0);
        otherUser.setGamesWon(0);
        otherUser.setTotalScores(0);

        game = new Game();
        game.setGameId(gameId);
        game.setGameMaster(gameMaster.getUserId());
        game.addNewPlayer(gameMaster);
        game.addNewPlayer(otherUser);
        game.setGameStatus(GameStatus.WAITING);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
    }

    @Test
    public void getGamePlayers_success() {
        when(gamePlayerService.getAllGameplayers()).thenReturn(Arrays.asList(new GamePlayer(), new GamePlayer()));
        List<GamePlayer> gamePlayers = gamePlayerService.getAllGameplayers();
        assertEquals(2, gamePlayers.size());
    }

    @Test
    public void getGamePlayer_found() {
        // Arrange
        GamePlayer foundPlayer = new GamePlayer();
        game.addNewPlayer(gameMaster);
        when(gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, 1L)).thenReturn(Optional.of(foundPlayer));

        // Act
        GamePlayer result = gamePlayerService.getGameplayer(gameId, 1L);

        // Assert
        assertEquals(foundPlayer, result);
    }

    @Test
    public void getGamePlayer_notFound() {
        UUID randomId = UUID.randomUUID();
        when(gamePlayerRepository.findByGame_GameIdAndUser_UserId(randomId, 1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> gamePlayerService.getGameplayer(randomId, 1L));
    }
}