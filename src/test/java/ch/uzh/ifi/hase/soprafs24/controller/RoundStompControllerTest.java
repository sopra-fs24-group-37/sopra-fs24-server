package ch.uzh.ifi.hase.soprafs24.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs24.entity.Round;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import ch.uzh.ifi.hase.soprafs24.rest.dto.stomp.GuessPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.GamePlayerService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs24.repository.RoundRepository;

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

    @BeforeEach
    public void setup() {
        gameId = UUID.randomUUID();
    }

    @Test
    public void testReceiveValidGuess() {
        GuessPostDTO guessDTO = new GuessPostDTO();
        guessDTO.setLat(47.3769);
        guessDTO.setLng(8.5417);
        guessDTO.setUserId(1L);
        guessDTO.setUseDoubleScore(false);

        Round mockRound = new Round();
        GamePlayer mockGamePlayer = new GamePlayer();
        when(roundService.getRound(gameId)).thenReturn(mockRound);
        when(gamePlayerService.getGameplayer(gameId, 1L)).thenReturn(mockGamePlayer);

        roundStompController.getGuesses(guessDTO, gameId);

        verify(roundRepository).save(mockRound);
        verify(gameService).updatePlayerScore(eq(gameId), eq(1L), anyInt());
    }

    @Test
    public void testReceiveGuessWithDoubleScore() {
        GuessPostDTO guessDTO = new GuessPostDTO();
        guessDTO.setLat(47.3769);
        guessDTO.setLng(8.5417);
        guessDTO.setUserId(1L);
        guessDTO.setUseDoubleScore(true);

        Round mockRound = new Round();
        GamePlayer mockGamePlayer = new GamePlayer();
        when(roundService.getRound(gameId)).thenReturn(mockRound);
        when(gamePlayerService.getGameplayer(gameId, 1L)).thenReturn(mockGamePlayer);
        when(gameService.useDoubleScorePowerUp(gameId, 1L)).thenReturn(true);

        roundStompController.getGuesses(guessDTO, gameId);

        verify(roundRepository).save(mockRound);
        verify(gameService).updatePlayerScore(eq(gameId), eq(1L), anyInt());
        assertNotNull(mockRound); // Additional assertions can be tailored to your actual class behavior
    }

    @Test
    public void testReceiveInvalidGuess() {
        GuessPostDTO guessDTO = new GuessPostDTO();
        guessDTO.setLat(47.3769);
        guessDTO.setLng(8.5417);
        guessDTO.setUserId(1L);
        guessDTO.setUseDoubleScore(false);

        Round mockRound = new Round();
        GamePlayer mockGamePlayer = new GamePlayer();
        when(roundService.getRound(gameId)).thenReturn(mockRound);
        when(gamePlayerService.getGameplayer(gameId, 1L)).thenReturn(mockGamePlayer);
        when(roundService.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(150.0); // Over 100 meters away

        roundStompController.getGuesses(guessDTO, gameId);

        verify(roundRepository).save(mockRound);
        verify(gameService, never()).updatePlayerScore(any(), any(), anyInt());
    }
}
