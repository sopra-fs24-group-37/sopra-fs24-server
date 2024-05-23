package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Round;
import ch.uzh.ifi.hase.soprafs24.entity.RoundStats;
import ch.uzh.ifi.hase.soprafs24.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoundStatsRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class RoundServiceTest {

    @Mock
    private RoundRepository roundRepository;
    @Mock
    private RoundStatsRepository roundStatsRepository;
    @Mock
    private GameService gameService;

    @InjectMocks
    private RoundService roundService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createRound_success() {
        // Arrange
        UUID gameId = UUID.randomUUID();
        Game game = new Game();
        game.setGameId(gameId);
        when(gameService.getGame(gameId)).thenReturn(game);

        // Act
        roundService.createRound(gameId);

        // Assert
        verify(roundRepository, times(1)).save(any(Round.class));
    }

    @Test
    public void createRound_failure() {
        // Arrange
        UUID gameId = UUID.randomUUID();
        Game game = new Game();
        game.setGameId(gameId);
        when(gameService.getGame(gameId)).thenReturn(game);

        // Act
        try {
            roundService.createRound(gameId);
        } catch (ResponseStatusException e) {
            // Assert
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        }
    }

    @Test
    public void getRound_success() {
        // Arrange
        UUID gameId = UUID.randomUUID();
        Round round = new Round();
        when(roundRepository.findById(gameId)).thenReturn(Optional.of(round));

        // Act
        Round result = roundService.getRound(gameId);

        // Assert
        assertEquals(round, result);
    }

    @Test
    public void getRound_failure() {
        // Arrange
        UUID gameId = UUID.randomUUID();
        when(roundRepository.findById(gameId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> roundService.getRound(gameId));
    }

    @Test
    public void updatePlayerGuess_success() {
        // Arrange
        UUID gameId = UUID.randomUUID();
        Long userId = 1L;
        Integer pointsInc = 1;
        double latitude = 47.399591;
        double longitude = 8.514325;
        RoundStats roundStats = new RoundStats();
        when(roundStatsRepository.findByGame_GameIdAndGamePlayer_PlayerId(gameId, userId)).thenReturn(Optional.of(roundStats));

        // Act
        roundService.updatePlayerGuess(gameId, userId, pointsInc, latitude, longitude);

        // Assert
        verify(roundStatsRepository, times(1)).save(any(RoundStats.class));
    }

    @Test
    public void updatePlayerGuess_failure() {
        // Arrange
        UUID gameId = UUID.randomUUID();
        Long userId = 1L;
        when(roundStatsRepository.findByGame_GameIdAndGamePlayer_PlayerId(gameId, userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> roundService.updatePlayerGuess(gameId, userId, 1, 47.399591, 8.514325));
    }
}