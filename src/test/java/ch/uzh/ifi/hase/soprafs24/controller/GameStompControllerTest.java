package ch.uzh.ifi.hase.soprafs24.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.stomp.GameSettingsPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;

import java.util.UUID;


@ExtendWith(MockitoExtension.class)
public class GameStompControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private RoundService roundService;

    @Mock
    private WebSocketService webSocketService;

    @InjectMocks
    private GameStompController gameStompController;

    private UUID gameId;

    @BeforeEach
    public void setup() {
        gameId = UUID.randomUUID();
    }

    @Test
    public void testGetLobbyInformation() {
        // Mocking
        Game mockGame = new Game();
        mockGame.setGameId(gameId);
        when(gameService.getGame(gameId)).thenReturn(mockGame);
    
        // Execution
        gameStompController.getLobbyInformation(gameId);
    
        // Verification
        verify(webSocketService).sendMessageToSubscribers(
            eq("/topic/games/" + gameId),
            any(GameGetDTO.class)
        );
    }
    
    @Test
    public void testGameStartedInfo() {
        // Execution
        gameStompController.gameStartedInfo(gameId);

        // Verification
        verify(roundService).createRound(gameId);
        verify(webSocketService).sendMessageToSubscribers("/topic/games/" + gameId + "/started", "Game has started");
    }

    @Test
    public void testSetLobbyInformation() {
        GameSettingsPostDTO settings = new GameSettingsPostDTO();
        settings.setNumRounds(3);
        settings.setGuessTime(20);
        settings.setSetGamePassword(true);

        when(gameService.getGame(gameId)).thenReturn(new Game());

        gameStompController.setLobbyInformation(settings, gameId);

        verify(gameService).updateGame(any(Game.class));
        verify(webSocketService).sendMessageToSubscribers(eq("/topic/games/" + gameId), any(GameGetDTO.class));
    }

    @Test
    public void testSetLobbyInformationWithoutPassword() {
        GameSettingsPostDTO settings = new GameSettingsPostDTO();
        settings.setNumRounds(3);
        settings.setGuessTime(20);
        settings.setSetGamePassword(false);

        when(gameService.getGame(gameId)).thenReturn(new Game());

        gameStompController.setLobbyInformation(settings, gameId);

        verify(gameService).updateGame(any(Game.class));
        verify(webSocketService).sendMessageToSubscribers(eq("/topic/games/" + gameId), any(GameGetDTO.class));
    }

    @Test
    public void testSetLobbyInformationInvalidValues() {
        GameSettingsPostDTO settings = new GameSettingsPostDTO();
        settings.setNumRounds(1);  // Invalid numRounds
        settings.setGuessTime(40); // Invalid guessTime
        settings.setSetGamePassword(false);

        when(gameService.getGame(gameId)).thenReturn(new Game());

        gameStompController.setLobbyInformation(settings, gameId);

        verify(gameService, never()).updateGame(any(Game.class));
        verify(webSocketService).sendMessageToSubscribers(eq("/topic/games/" + gameId), any(GameGetDTO.class));
    }

    @Test
    public void testSetLobbyInformationAllEmpty() {
        GameSettingsPostDTO settings = new GameSettingsPostDTO();
        settings.setNumRounds(0);
        settings.setGuessTime(0);
        settings.setSetGamePassword(false);

        when(gameService.getGame(gameId)).thenReturn(new Game());

        gameStompController.setLobbyInformation(settings, gameId);

        verify(gameService, never()).updateGame(any(Game.class));
        verify(webSocketService).sendMessageToSubscribers(eq("/topic/games/" + gameId), any(GameGetDTO.class));
    }

}

