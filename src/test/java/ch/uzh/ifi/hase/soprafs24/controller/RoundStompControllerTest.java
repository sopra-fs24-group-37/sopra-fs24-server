package ch.uzh.ifi.hase.soprafs24.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs24.entity.Round;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import ch.uzh.ifi.hase.soprafs24.repository.RoundRepository;
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
