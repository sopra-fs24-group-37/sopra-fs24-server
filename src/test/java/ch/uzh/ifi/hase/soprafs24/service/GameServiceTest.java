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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GamePlayerRepository gamePlayerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private GameService gameService;

    private UUID gameId;
    private User gameMaster;
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

        game = new Game();
        game.setGameId(gameId);
        game.setGameMaster(gameMaster.getUserId());
        game.setPlayers(new HashSet<>());
        game.setGameStatus(GameStatus.WAITING);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
    }

    @Test
    public void getGames_success() {
        when(gameRepository.findAll()).thenReturn(Arrays.asList(new Game(), new Game()));
        List<Game> games = gameService.getGames();
        assertEquals(2, games.size());
    }

    @Test
    public void getGame_found() {
        Game foundGame = gameService.getGame(gameId);
        assertNotNull(foundGame);
        assertEquals(gameId, foundGame.getGameId());
    }

    @Test
    public void getGame_notFound() {
        UUID randomId = UUID.randomUUID();
        when(gameRepository.findById(randomId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> gameService.getGame(randomId));
    }

    @Test
    public void calculateLeaderboard_withPlayers() {
        // Test case setup
        GamePlayer gp1 = new GamePlayer(game, gameMaster, 100);
        game.getPlayers().add(gp1);

        // Method invocation
        Game calculatedGame = gameService.calculateLeaderboard(gameId);

        // Assertion
        assertNotNull(calculatedGame);
        assertFalse(calculatedGame.getPlayers().isEmpty());
        assertEquals(100, calculatedGame.getPlayers().iterator().next().getScore());
    }

    @Test
    public void calculateLeaderboard_noPlayers() {
        // Test case setup: no players added to the game
        game.setPlayers(new HashSet<>());

        // Method invocation
        Game calculatedGame = gameService.calculateLeaderboard(gameId);

        // Assertion
        assertNotNull(calculatedGame);
        assertTrue(calculatedGame.getPlayers().isEmpty());
    }

    @Test
    public void updateUserStatistics_updates() {
        // Test case setup
        GamePlayer gp1 = new GamePlayer(game, gameMaster, 30);
        gp1.setScore(150);
        Set<GamePlayer> players = new HashSet<>(Collections.singletonList(gp1));
        game.setPlayers(players);

        // Method invocation
        gameService.updateUserStatistics(gameId);

        // Verification
        verify(userRepository, times(1)).save(any(User.class));
    }

    // @Test
    // public void createGame_success() {
    //     // Test case setup
    //     when(userService.findUserbyId(anyLong())).thenReturn(gameMaster);

    //     // Method invocation
    //     Game newGame = gameService.createGame(gameMaster.getUserId());

    //     // Assertion
    //     assertNotNull(newGame);
    //     assertEquals(GameStatus.WAITING, newGame.getGameStatus());
    // }

    @Test
    public void startGame_success() {
        // Test case setup
        User user2 = new User();
        user2.setUserId(2L);
    
        game.setPlayers(new HashSet<>(Arrays.asList(new GamePlayer(game, gameMaster, Integer.valueOf(0)), new GamePlayer(game, user2, Integer.valueOf(0)))));
    
        // Method invocation and assertion
        assertEquals(GameStatus.STARTED, gameService.startGame(gameId).getGameStatus());
    }
    

    @Test
    public void startGame_alreadyStarted() {
        // Test case setup
        game.setGameStatus(GameStatus.STARTED);

        // Method invocation and assertion
        assertThrows(ResponseStatusException.class, () -> gameService.startGame(gameId));
    }

    @Test
    public void startGame_alreadyEnded() {
        // Test case setup
        game.setGameStatus(GameStatus.ENDED);

        // Method invocation and assertion
        assertThrows(ResponseStatusException.class, () -> gameService.startGame(gameId));
    }

    @Test
    public void startGame_notWaiting() {
        // Test case setup
        game.setGameStatus(GameStatus.STARTED);

        // Method invocation and assertion
        assertThrows(ResponseStatusException.class, () -> gameService.startGame(gameId));
    }

    // Other tests for remaining endpoints
}
