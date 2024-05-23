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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private Long userId;
    private User gameMaster;
    private Game game;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        gameId = UUID.randomUUID();
        gameMaster = new User();
        gameMaster.setUserId(userId);
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
    public void calculateNumPlayers_returnsCorrectCount() {
        // Setup
        GamePlayer player1 = new GamePlayer(game, gameMaster, 10);
        GamePlayer player2 = new GamePlayer(game, new User(), 20);
        Set<GamePlayer> players = new HashSet<>(Arrays.asList(player1, player2));
        game.setPlayers(players);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        // Invocation
        int numPlayers = gameService.getNumPlayers(gameId);

        // Assertion
        assertEquals(2, numPlayers);
    }

    @Test
    public void calculateNumPlayers_returnsNoPlayer() {
        // Setup
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        // Invocation
        int numPlayers = gameService.getNumPlayers(gameId);

        // Assertion
        assertEquals(0, numPlayers);
    }

    @Test
    public void calculateNumPlayers_gameNotFound() {
        // Setup
        UUID nonExistentGameId = UUID.randomUUID();
        when(gameRepository.findById(nonExistentGameId)).thenReturn(Optional.empty());
    
        // Invocation and Assertion
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> gameService.getNumPlayers(nonExistentGameId));
    
        // Assertion on the exception details
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game not found!", exception.getReason());
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

    @Test
    public void createGame_Success() {
        when(userService.findUserbyId(anyLong())).thenReturn(gameMaster);

        Game createdGame = new Game();
        createdGame.setGameId(gameId);
        createdGame.setGameMaster(gameMaster.getUserId());
        createdGame.setGameStatus(GameStatus.WAITING);
        createdGame.addNewPlayer(gameMaster);

        when(gameRepository.save(any(Game.class))).thenReturn(createdGame);

        Game newGame = gameService.createGame(gameMaster.getUserId());

        assertNotNull(newGame);
        assertEquals(gameId, newGame.getGameId());
        assertEquals(GameStatus.WAITING, newGame.getGameStatus());
        assertEquals(1, newGame.getPlayers().size());
        verify(gameRepository).save(any(Game.class));
    }

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


    @Test
    public void EndGame_Success() {
        // Arrange
        when(gameRepository.findById(gameId)).thenReturn(java.util.Optional.of(game));

        // Act
        Game endedGame = gameService.endGame(gameId);

        // Assert
        assertNotNull(endedGame);
        assertEquals(GameStatus.ENDED, endedGame.getGameStatus());
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void EndGame_AlreadyEnded() {
        // Arrange
        game.setGameStatus(GameStatus.ENDED);
        when(gameRepository.findById(gameId)).thenReturn(java.util.Optional.of(game));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.endGame(gameId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Game already ended!", exception.getReason());
        verify(gameRepository, never()).save(game);
    }


    @Test
    public void useDoubleScorePowerUp_available_success() {
        // Test case setup
        UUID gameId = UUID.randomUUID();
        Long userId = 1L;
        GamePlayer gamePlayer = new GamePlayer();
        gamePlayer.setDoubleScore(true);

        when(gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)).thenReturn(Optional.of(gamePlayer));

        // Method invocation
        boolean result = gameService.useDoubleScorePowerUp(gameId, userId);

        // Assertion
        assertTrue(result);
        assertFalse(gamePlayer.getDoubleScore()); // Ensure the power-up is used
        verify(gamePlayerRepository, times(1)).save(any(GamePlayer.class));
    }

    @Test
    public void useDoubleScorePowerUp_notAvailable_failure() {
        // Test case setup
        UUID gameId = UUID.randomUUID();
        Long userId = 1L;
        GamePlayer gamePlayer = new GamePlayer();
        gamePlayer.setDoubleScore(false); // Power-up not available

        when(gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)).thenReturn(Optional.of(gamePlayer));

        // Method invocation
        boolean result = gameService.useDoubleScorePowerUp(gameId, userId);

        // Assertion
        assertFalse(result);
        assertFalse(gamePlayer.getDoubleScore()); // Ensure the power-up remains unused
        verify(gamePlayerRepository, never()).save(any(GamePlayer.class)); // Verify that save method was not called
    }

    @Test
    public void leaveGame_UserInGame_RemovesUserFromGame() {
        Game game = new Game();
        game.setGameId(gameId);

        User user = new User();
        user.setUserId(2L);

        GamePlayer gamePlayer = new GamePlayer();
        gamePlayer.setUser(user);
        game.getPlayers().add(gamePlayer);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userService.findUserbyId(2L)).thenReturn(user);

        Game updatedGame = gameService.leaveGame(gameId, 2L);

        assertNotNull(updatedGame);
        assertTrue(updatedGame.getPlayers().isEmpty());
        verify(gamePlayerRepository).delete(any(GamePlayer.class));
        verify(gameRepository).save(game);
    }

    @Test
    public void leaveGame_UserNotInGame_ThrowsException() {
        Game game = new Game();
        game.setGameId(gameId);

        User user = new User();
        user.setUserId(2L);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userService.findUserbyId(2L)).thenReturn(user);

        assertThrows(ResponseStatusException.class, () -> gameService.leaveGame(gameId, 2L));
        verify(gamePlayerRepository, never()).delete(any(GamePlayer.class));
    }

    @Test
    public void leaveGame_GameNotFound_ThrowsException() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> gameService.leaveGame(gameId, 2L));
    }

    @Test
    public void findGamebyIdGameExists() {
        // Arrange
        Game mockGame = new Game();
        mockGame.setGameId(gameId);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(mockGame));

        // Act
        Game foundGame = gameService.findGamebyId(gameId);

        // Assert
        assertEquals(mockGame, foundGame);
        verify(gameRepository).findById(gameId);
    }

    @Test
    public void findGamebyIdGameNotFound() {
        // Arrange
        UUID gameId = UUID.randomUUID();
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> gameService.findGamebyId(gameId),
            "Game with user ID " + gameId + " was not found!"
        );

        // Check correct status code
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(gameRepository).findById(gameId);
    }

    @Test
    public void updateGameSuccess() {
        // Arrange
        when(gameRepository.saveAndFlush(game)).thenReturn(game);
    
        // Act
        gameService.updateGame(game);
    
        // Assert
        verify(gameRepository).saveAndFlush(game);
    }
    
    @Test
    public void updateGameHandlesException() {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(gameRepository).saveAndFlush(game);
    
        // Act & Assert
        assertThrows(RuntimeException.class, () -> gameService.updateGame(game));
    }

    @Test
    public void deleteGameByIdGameExists() {
        // Arrange
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
    
        // Act
        gameService.deleteGameById(gameId);
    
        // Assert
        verify(gameRepository).deleteById(gameId);
    }
    
    @Test
    public void deleteGameByIdGameNotFound() {
        // Arrange
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());
    
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> gameService.deleteGameById(gameId));
    }

    @Test
    public void useCantonHintPowerUpSuccess() {
        // Arrange
        Long userId = 1L;
        GamePlayer gamePlayer = new GamePlayer();
        gamePlayer.setCantonHint(true);
        when(gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)).thenReturn(Optional.of(gamePlayer));
    
        // Act
        Boolean result = gameService.useCantonHintPowerUp(gameId, userId);
    
        // Assert
        assertTrue(result);
        verify(gamePlayerRepository).save(gamePlayer);
    }
    
    @Test
    public void useCantonHintPowerUpFail() {
        // Arrange
        Long userId = 1L;
        GamePlayer gamePlayer = new GamePlayer();
        gamePlayer.setCantonHint(false);
        when(gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)).thenReturn(Optional.of(gamePlayer));
    
        // Act
        Boolean result = gameService.useCantonHintPowerUp(gameId, userId);
    
        // Assert
        assertFalse(result);
    }
    
    @Test
    public void useCantonHintPowerUpNotFound() {
        // Arrange
        Long userId = 1L;
        when(gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)).thenReturn(Optional.empty());
    
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> gameService.useCantonHintPowerUp(gameId, userId));
    }
    
    @Test
    public void useMultipleCantonHintPowerUpSuccess() {
        // Arrange
        Long userId = 1L;
        GamePlayer gamePlayer = new GamePlayer();
        gamePlayer.setMultipleCantonHint(true);
        when(gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)).thenReturn(Optional.of(gamePlayer));
    
        // Act
        Boolean result = gameService.useMultipleCantonHintPowerUp(gameId, userId);
    
        // Assert
        assertTrue(result);
        verify(gamePlayerRepository).save(gamePlayer);
    }
    
    @Test
    public void useMultipleCantonHintPowerUpFail() {
        // Arrange
        Long userId = 1L;
        GamePlayer gamePlayer = new GamePlayer();
        gamePlayer.setMultipleCantonHint(false);
        when(gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)).thenReturn(Optional.of(gamePlayer));
    
        // Act
        Boolean result = gameService.useMultipleCantonHintPowerUp(gameId, userId);
    
        // Assert
        assertFalse(result);
    }
    
    @Test
    public void useMultipleCantonHintPowerUpNotFound() {
        // Arrange
        Long userId = 1L;
        when(gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)).thenReturn(Optional.empty());
    
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> gameService.useMultipleCantonHintPowerUp(gameId, userId));
    }
    
    @Test
    public void endGameSuccess() {
        // Arrange
        Game game = new Game();
        game.setGameStatus(GameStatus.STARTED);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
    
        // Act
        Game endedGame = gameService.endGame(gameId);
    
        // Assert
        assertEquals(GameStatus.ENDED, endedGame.getGameStatus());
        verify(gameRepository).save(endedGame);
    }
    
    @Test
    public void endGameAlreadyEnded() {
        // Arrange
        Game game = new Game();
        game.setGameStatus(GameStatus.ENDED);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
    
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> gameService.endGame(gameId));
    }

    @Test
    public void joinGameSuccessfullyNoPassword() {
        // Arrange
        UUID gameId = UUID.randomUUID();
        Long userId = 1L;
        Game game = mock(Game.class); // Mock the Game object
        when(game.getGameStatus()).thenReturn(GameStatus.WAITING); // Mock necessary properties
        when(game.getPassword()).thenReturn(null); // Explicitly handling password check
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        
        User user = new User();
        user.setUserId(userId);
        when(userService.findUserbyId(userId)).thenReturn(user);
    
        // Act
        Game joinedGame = gameService.joinGame(gameId, userId, null);
    
        // Assert
        verify(gameRepository).save(game);
        assertNotNull(joinedGame);
    }
    
    
    @Test
    public void joinGameSuccessfullyWithPassword() {
        // Arrange
        Game game = new Game();
        game.setGameStatus(GameStatus.WAITING);
        game.setPassword(1234);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        User user = new User();
        user.setUserId(userId);
        when(userService.findUserbyId(userId)).thenReturn(user);
    
        // Act
        Game joinedGame = gameService.joinGame(gameId, userId, 1234);
    
        // Assert
        verify(gameRepository).save(game);
        assertEquals(game, joinedGame);
    }
    
    @Test
    public void joinGameGameNotFound() {
        // Arrange
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());
    
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(gameId, userId, null));
    }
    
    @Test
    public void joinGameWrongPassword() {
        // Arrange
        Game game = new Game();
        game.setPassword(1234);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
    
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(gameId, userId, 1111));
    }
    
    @Test
    public void joinGameNoPasswordExpectedButProvided() {
        // Arrange
        Game game = new Game();
        game.setPassword(null);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
    
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(gameId, userId, 1234));
    }
    
    @Test
    public void joinGameUserAlreadyInGame() {
        // Arrange
        Game game = new Game();
        User user = new User();
        user.setUserId(userId);
        GamePlayer gamePlayer = new GamePlayer();
        gamePlayer.setUser(user);
        game.setPlayers(Collections.singleton(gamePlayer));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userService.findUserbyId(userId)).thenReturn(user);
    
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(gameId, userId, null));
    }
    
}