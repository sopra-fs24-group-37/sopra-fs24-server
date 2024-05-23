package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Round;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoundDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoundControllerTest {

    @Mock
    private RoundService roundService;

    @InjectMocks
    private RoundController roundController;

    private UUID gameId;
    private Round round;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        gameId = UUID.randomUUID();
        round = new Round();
        round.setGameId(gameId);
    }

    @Test
    public void getLeaderboard_success() {
        // Arrange
        when(roundService.getRound(gameId)).thenReturn(round);
        RoundDTO roundDTO = DTOMapper.INSTANCE.convertEntityToRoundDTO(round);

        // Act
        ResponseEntity<RoundDTO> responseEntity = roundController.getLeaderboard(gameId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(gameId, responseEntity.getBody().getGameId());
        verify(roundService, times(1)).getRound(gameId);
    }

}
