package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePlayerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class GameControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private RoundService roundService;

    @InjectMocks
    private GameController gameController;

    private Game game;
    private UUID gameId;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        gameId = UUID.randomUUID();
        game = new Game();
        game.setGameId(gameId);
    }

    @Test
    public void getAllGames_success() {
        // Arrange
        Game game1 = new Game();
        Game game2 = new Game();
        List<Game> games = Arrays.asList(game1, game2);

        when(gameService.getGames()).thenReturn(games);

        // Act
        List<GameGetDTO> gameGetDTOs = gameController.getAllGames();

        // Assert
        assertNotNull(gameGetDTOs);
        assertEquals(2, gameGetDTOs.size());
        verify(gameService, times(1)).getGames();
    }

    @Test
    public void getGameById_success() {
        // Arrange
        when(gameService.getGame(gameId)).thenReturn(game);

        // Act
        GameGetDTO gameGetDTO = gameController.getGameById(gameId);

        // Assert
        assertNotNull(gameGetDTO);
        assertEquals(gameId, gameGetDTO.getGameId());
        verify(gameService, times(1)).getGame(gameId);
    }

    @Test
    public void deleteGame_success() {
        // Act
        ResponseEntity<?> responseEntity = gameController.deleteGame(gameId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(gameService, times(1)).deleteGameById(gameId);
    }

    @Test
    public void createGame_success() {
        // Arrange
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setGameMaster(1L);

        when(gameService.createGame(1L)).thenReturn(game);

        // Act
        ResponseEntity<GameGetDTO> responseEntity = gameController.createGame(gamePostDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(gameId, responseEntity.getBody().getGameId());
        verify(gameService, times(1)).createGame(1L);
    }

    @Test
    public void startGame_success() {
        // Arrange
        when(gameService.startGame(gameId)).thenReturn(game);

        // Act
        ResponseEntity<GameGetDTO> responseEntity = gameController.startGame(gameId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(gameId, responseEntity.getBody().getGameId());
        verify(gameService, times(1)).startGame(gameId);
        verify(roundService, times(1)).createRound(gameId);
    }

    @Test
    public void endGame_success() {
        // Arrange
        when(gameService.endGame(gameId)).thenReturn(game);

        // Act
        ResponseEntity<GameGetDTO> responseEntity = gameController.endGame(gameId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(gameId, responseEntity.getBody().getGameId());
        verify(gameService, times(1)).endGame(gameId);
    }

    @Test
    public void joinGame_success() {
        // Arrange
        Long userId = 1L;
        Integer gamePassword = 1234;
        when(gameService.joinGame(gameId, userId, gamePassword)).thenReturn(game);

        // Act
        ResponseEntity<Game> responseEntity = gameController.joinGame(gameId, userId, gamePassword);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(gameId, responseEntity.getBody().getGameId());
        verify(gameService, times(1)).joinGame(gameId, userId, gamePassword);
    }

    @Test
    public void leaveGame_success() {
        // Arrange
        Long userId = 1L;
        game.setPlayers(new HashSet<>(Arrays.asList(new GamePlayer())));
        when(gameService.leaveGame(gameId, userId)).thenReturn(game);

        // Act
        ResponseEntity<Game> responseEntity = gameController.leaveGame(gameId, userId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(gameId, responseEntity.getBody().getGameId());
        verify(gameService, times(1)).leaveGame(gameId, userId);
    }

    @Test
    public void leaveGame_deleteWhenEmpty() {
        // Arrange
        Long userId = 1L;
        when(gameService.leaveGame(gameId, userId)).thenReturn(game);

        // Act
        ResponseEntity<Game> responseEntity = gameController.leaveGame(gameId, userId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(gameService, times(1)).leaveGame(gameId, userId);
        verify(gameService, times(1)).deleteGameById(gameId);
    }

    @Test
    public void getLeaderboard_success() {
        // Arrange
        game.setPlayers(new HashSet<>(Arrays.asList(new GamePlayer(), new GamePlayer())));
        when(gameService.calculateLeaderboard(gameId)).thenReturn(game);

        // Act
        ResponseEntity<LeaderboardDTO> responseEntity = gameController.getLeaderboard(gameId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody().getWinners());
        verify(gameService, times(1)).calculateLeaderboard(gameId);
    }
}