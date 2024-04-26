package ch.uzh.ifi.hase.soprafs24.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardDTO;

import java.util.UUID;
import java.util.List;

public class GameControllerTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    private UUID game111;
    private UUID game000;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        game111 = UUID.randomUUID();
        game000 = UUID.randomUUID();
    }

    // Test cases for createGame
    @Test
    public void testCreateGameSuccess() {
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setGameMaster(1L);  // Assume the master ID is set correctly

        Game mockGame = new Game();
        mockGame.setGameId(game111);
        Mockito.when(gameService.createGame(1L)).thenReturn(mockGame);

        ResponseEntity<GameGetDTO> response = gameController.createGame(gamePostDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(game111, response.getBody().getGameId());
    }

    @Test
    public void testCreateGameFailure() {
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setGameMaster(1L);  // Assume the master ID is set

        Mockito.when(gameService.createGame(1L)).thenReturn(null);

        ResponseEntity<GameGetDTO> response = gameController.createGame(gamePostDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // Test cases for startGame
    @Test
    public void testStartGameSuccess() {
        Game mockGame = new Game();
        mockGame.setGameId(game111);
        Mockito.when(gameService.startGame(game111)).thenReturn(mockGame);

        ResponseEntity<GameGetDTO> response = gameController.startGame(game111);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(game111, response.getBody().getGameId());
    }

    @Test
    public void testStartGameFailure() {
        Mockito.when(gameService.startGame(game000)).thenReturn(null);

        ResponseEntity<GameGetDTO> response = gameController.startGame(game000);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Test cases for calculateLeaderboard
    // @Test
    // public void testCalculateLeaderboardSuccess() {
    //     Game mockGame = new Game();
    //     mockGame.setGameId(game111);
    //     LeaderboardDTO mockLeaderboardDTO = new LeaderboardDTO();
    //     Mockito.when(gameService.calculateLeaderboard(game111)).thenReturn(mockGame);

    }