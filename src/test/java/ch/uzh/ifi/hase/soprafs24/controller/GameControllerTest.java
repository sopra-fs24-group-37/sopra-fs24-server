package ch.uzh.ifi.hase.soprafs24.controller;

import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePlayerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class GameControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private RoundService roundService;

    @Mock
    private GamePostDTO gamePostDTO;


    @InjectMocks
    private GameController gameController;

    
    private UUID gameId;
    private Game game;
    private GameGetDTO gameGetDTO;
    private Long userId;
    private User user;
    private List<Game> games;

    @BeforeEach
    public void setup() {
        gameId = UUID.randomUUID();
        userId = 1L;

        game = new Game();
        game.setGameId(gameId);

        gameGetDTO = new GameGetDTO();
        gameGetDTO.setGameId(gameId);

        gamePostDTO = new GamePostDTO();
        gamePostDTO.setGameMaster(userId);

        user = new User();
        user.setUserId(userId);

        games = Arrays.asList(game);
    }

    // @Test
    // public void testGetAllGames() {
    //     when(gameService.getGames()).thenReturn(games);
    //     when(DTOMapper.INSTANCE.convertEntityToGameGetDTO(any(Game.class))).thenReturn(gameGetDTO);

    //     List<GameGetDTO> result = gameController.getAllGames();
    //     assertEquals(1, result.size());
    //     assertEquals(gameId, result.get(0).getGameId());
    // }

    @Test
    public void testGetAllGames_NoGames() {
        when(gameService.getGames()).thenReturn(Collections.emptyList());

        List<GameGetDTO> result = gameController.getAllGames();
        assertTrue(result.isEmpty());
    }

    // @Test
    // public void testGetGameById() {
    //     when(gameService.getGame(gameId)).thenReturn(game);
    //     when(DTOMapper.INSTANCE.convertEntityToGameGetDTO(any(Game.class))).thenReturn(gameGetDTO);

    //     GameGetDTO result = gameController.getGameById(gameId);
    //     assertEquals(gameId, result.getGameId());
    // }

    @Test
    public void testGetGameById_NotFound() {
        when(gameService.getGame(gameId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class, () -> gameController.getGameById(gameId));
    }

    @Test
    public void testDeleteGame() {
        doNothing().when(gameService).deleteGameById(gameId);

        ResponseEntity<?> response = gameController.deleteGame(gameId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Game Deleted", response.getBody());
    }

    @Test
    public void testDeleteGame_NotFound() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(gameService).deleteGameById(gameId);

        assertThrows(ResponseStatusException.class, () -> gameController.deleteGame(gameId));
    }

    // @Test
    // public void testCreateGame() {
    //     // Setup
    //     Long gameMasterId = 1L;
    //     when(gamePostDTO.getGameMaster()).thenReturn(gameMasterId); // Ensure GamePostDTO returns a predictable ID
    //     when(gameService.createGame(gameMasterId)).thenReturn(game); // Return a real Game object configured for testing
        
    //     // Configure the game object for predictable DTO mapping
    //     game.setGameId(UUID.randomUUID()); // Set a random UUID for the game
    //     game.setGameMaster(gameMasterId);  // Set the game master as expected
    //     // Assume game has other properties set as needed for mapping

    //     // Execution
    //     ResponseEntity<GameGetDTO> response = gameController.createGame(gamePostDTO);

    //     // Verification
    //     assertEquals(HttpStatus.CREATED, response.getStatusCode());
    //     assertNotNull(response.getBody());
    //     assertEquals(game.getGameId(), response.getBody().getGameId()); // Directly compare expected game ID with DTO
    //     verify(gameService).createGame(gameMasterId); // Verify service was called with correct ID
    // }
    
    @Test
    public void testCreateGame_Failure() {
        when(gameService.createGame(anyLong())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    
        assertThrows(ResponseStatusException.class, () -> gameController.createGame(gamePostDTO));
    }

    // @Test
    // public void testStartGame() {
    //     when(gameService.startGame(gameId)).thenReturn(game);
    //     when(DTOMapper.INSTANCE.convertEntityToGameGetDTO(any(Game.class))).thenReturn(gameGetDTO);

    //     ResponseEntity<GameGetDTO> response = gameController.startGame(gameId);
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertEquals(gameId, response.getBody().getGameId());
    // }

    @Test
    public void testStartGame_NotFound() {
        when(gameService.startGame(gameId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class, () -> gameController.startGame(gameId));
    }

    // @Test
    // public void testEndGame() {
    //     when(gameService.endGame(gameId)).thenReturn(game);
    //     when(DTOMapper.INSTANCE.convertEntityToGameGetDTO(any(Game.class))).thenReturn(gameGetDTO);

    //     ResponseEntity<GameGetDTO> response = gameController.endGame(gameId);
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertEquals(gameId, response.getBody().getGameId());
    // }

    @Test
    public void testEndGame_NotFound() {
        when(gameService.endGame(gameId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class, () -> gameController.endGame(gameId));
    }

    @Test
    public void testJoinGame() {
        when(gameService.joinGame(eq(gameId), eq(userId), any())).thenReturn(game); // Ensure all arguments use matchers
    
        ResponseEntity<Game> response = gameController.joinGame(gameId, userId, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(gameId, response.getBody().getGameId());
    }
    
    @Test
    public void testJoinGame_NotFound() {
        when(gameService.joinGame(eq(gameId), eq(userId), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
    
        assertThrows(ResponseStatusException.class, () -> gameController.joinGame(gameId, userId, null));
    }

    // @Test
    // public void testLeaveGame() {
    //     when(gameService.leaveGame(gameId, userId)).thenReturn(game);

    //     ResponseEntity<Game> response = gameController.leaveGame(gameId, userId);
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertEquals(gameId, response.getBody().getGameId());
    // }

    @Test
    public void testLeaveGame_NotFound() {
        when(gameService.leaveGame(gameId, userId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class, () -> gameController.leaveGame(gameId, userId));
    }

    // @Test
    // public void testGetLeaderboard() {
    //     GamePlayer player1 = new GamePlayer();
    //     player1.setScore(100);
    //     GamePlayer player2 = new GamePlayer();
    //     player2.setScore(200);
    //     game.setPlayers(new LinkedHashSet<>(Arrays.asList(player1, player2)));

    //     when(gameService.calculateLeaderboard(gameId)).thenReturn(game);
    //     when(DTOMapper.INSTANCE.convertEntityToLeaderboardDTO(any(Game.class))).thenReturn(new LeaderboardDTO());

    //     ResponseEntity<LeaderboardDTO> response = gameController.getLeaderboard(gameId);
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertNotNull(response.getBody().getWinners());
    // }

    // @Test
    // public void testGetLeaderboard_Empty() {
    //     when(gameService.calculateLeaderboard(gameId)).thenReturn(game);
    //     when(DTOMapper.INSTANCE.convertEntityToLeaderboardDTO(any(Game.class))).thenReturn(new LeaderboardDTO());

    //     ResponseEntity<LeaderboardDTO> response = gameController.getLeaderboard(gameId);
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertTrue(response.getBody().getWinners().isEmpty());
    // }
}
