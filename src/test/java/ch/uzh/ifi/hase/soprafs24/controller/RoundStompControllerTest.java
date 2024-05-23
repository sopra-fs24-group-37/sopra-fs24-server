package ch.uzh.ifi.hase.soprafs24.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Round;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.stomp.GuessPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RoundStompControllerTest {

    @Mock
    private RoundService roundService;
    @Mock
    private WebSocketService webSocketService;
    @Mock
    private GameService gameService;
    @Mock
    private GamePlayerService gamePlayerService;
    @Mock
    private RoundRepository roundRepository;

    @InjectMocks
    private RoundStompController roundStompController;

    private UUID gameId;
    private Long userId;

    @BeforeEach
    public void setup() {
        gameId = UUID.randomUUID();
        userId = 1L;
    }

    @Test
    public void testGetNewRound_EndGame() {
        // Set the Game
        UUID gameId = UUID.randomUUID();
        Game mockGame = new Game();
        mockGame.setGameStatus(GameStatus.STARTED);
        mockGame.setNumRounds(4);

        Round mockRound = new Round();
        mockRound.setRoundsPlayed(5);

        when(roundService.getRound(gameId)).thenReturn(mockRound);
        when(gameService.getGame(gameId)).thenReturn(mockGame);
        when(gameService.getNumPlayers(gameId)).thenReturn(2);

        // Act
        roundStompController.getNewRound(gameId);

        // Assert
        verify(gameService).updateUserStatistics(gameId);
        verify(gameService).endGame(gameId);
    }
    @Test
    public void testGetNewRound_GameAlreadyEnded() {
        // Set the Game
        UUID gameId = UUID.randomUUID();
        Game mockGame = new Game();
        mockGame.setGameStatus(GameStatus.ENDED);
        mockGame.setNumRounds(4);

        Round mockRound = new Round();
        mockRound.setRoundsPlayed(5);

        when(roundService.getRound(gameId)).thenReturn(mockRound);
        when(gameService.getGame(gameId)).thenReturn(mockGame);
        when(gameService.getNumPlayers(gameId)).thenReturn(2);

        // Act
        roundStompController.getNewRound(gameId);

        // Assert
        verify(webSocketService).sendMessageToSubscribers("/topic/games/" + gameId + "/ended", "Game has already ended");
    }

    @Test
    public void testGetNewRound_NonEndGame() {
        // Arrange
        Game mockGame = new Game();
        mockGame.setGameStatus(GameStatus.STARTED);
        mockGame.setNumRounds(3);

        Round mockRound = new Round();
        mockRound.setRoundsPlayed(1);
        mockRound.setCheckIn(2);

        when(roundService.getRound(gameId)).thenReturn(mockRound);
        when(gameService.getGame(gameId)).thenReturn(mockGame);
        when(gameService.getNumPlayers(gameId)).thenReturn(3);
        when(roundService.getRandomPicture(mockRound,mockGame)).thenReturn("pleasework");

        // Act
        roundStompController.getNewRound(gameId);

        // Assert
        verify(roundService).getRound(gameId);
        verify(gameService, never()).endGame(gameId);
        verify(roundRepository, times(2)).save(any());
        verify(webSocketService).sendMessageToSubscriberswithoutLog(eq("/topic/games/" + gameId +"/round"),eq("pleasework"));
    }

    @Test
    public void testReceiveGuessWithHints() {
        // Set up the guess DTO
        GuessPostDTO guessDTO = new GuessPostDTO();
        guessDTO.setLat(47.3769);
        guessDTO.setLng(8.5417);
        guessDTO.setUserId(userId);
        guessDTO.setUseDoubleScore(false);
        guessDTO.setUseCantonHint(true);
        guessDTO.setUseMultipleCantonHint(false);

        // Set up the mock round and game player
        Round mockRound = new Round();
        mockRound.setLatitude(47.3769);
        mockRound.setLongitude(8.5417);
        GamePlayer mockGamePlayer = new GamePlayer();
        when(roundService.getRound(gameId)).thenReturn(mockRound);
        when(gamePlayerService.getGameplayer(gameId, userId)).thenReturn(mockGamePlayer);
        when(roundService.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(10.0);
        when(gameService.useCantonHintPowerUp(gameId, userId)).thenReturn(true);

        // Call the method under test
        roundStompController.getGuesses(guessDTO, gameId);

        // Verify interactions
        verify(gameService).updatePlayerScore(eq(gameId), eq(userId), anyInt());
        verify(gameService).useCantonHintPowerUp(gameId, userId);
        verify(gameService, never()).useMultipleCantonHintPowerUp(gameId, userId);
        assertNotNull(mockRound);
    }

    @Test
    public void testReceiveGuessWithMultiplePowerUps() {
        // Similar setup but with multiple power-ups
        GuessPostDTO guessDTO = new GuessPostDTO();
        guessDTO.setLat(47.3769);
        guessDTO.setLng(8.5417);
        guessDTO.setUserId(userId);
        guessDTO.setUseDoubleScore(true);
        guessDTO.setUseCantonHint(false);
        guessDTO.setUseMultipleCantonHint(true);

        Round mockRound = new Round();
        GamePlayer mockGamePlayer = new GamePlayer();
        when(roundService.getRound(gameId)).thenReturn(mockRound);
        when(gamePlayerService.getGameplayer(gameId, userId)).thenReturn(mockGamePlayer);
        when(roundService.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(5.0);
        when(gameService.useDoubleScorePowerUp(gameId, userId)).thenReturn(true);
        when(gameService.useMultipleCantonHintPowerUp(gameId, userId)).thenReturn(true);

        roundStompController.getGuesses(guessDTO, gameId);

        verify(gameService).updatePlayerScore(eq(gameId), eq(userId), anyInt());
        verify(gameService, never()).useCantonHintPowerUp(gameId, userId);
        verify(gameService).useMultipleCantonHintPowerUp(gameId, userId);
    }
}
