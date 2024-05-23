package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.config.ApiKeyConfig;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoundStatsRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        User mockuser = new User();
        game.setGameId(gameId);
        game.addNewPlayer(mockuser);
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

    /*
    @Test
    public void getRandomPicture_success() throws Exception {
        // Arrange
        RoundService roundServiceSpy = spy(roundService);
        UUID gameId = UUID.randomUUID();
        Game game = new Game();
        game.setGameId(gameId);
        game.setGuessTime(30);
        Round round = new Round();
        JSONObject jsonResponse = new JSONObject("{ \"location\": { \"position\": { \"latitude\": 47.399591, \"longitude\": 8.514325 } }, \"urls\": { \"regular\": \"some_url\" }, \"user\": { \"name\": \"user_name\", \"username\": \"user_username\" } }");

        when(gameService.getGame(gameId)).thenReturn(game);
        PowerMockito.when(roundService, "fetchPictureFromApi").thenThrow(new IOException());
        when(roundRepository.save(any(Round.class))).thenReturn(round);

        // Act
        String result = roundServiceSpy.getRandomPicture(round, game);

        // Assert
        verify(roundRepository, times(1)).save(round);
        assertTrue(result.contains("some_url"));
        assertTrue(result.contains("user_name"));
        assertTrue(result.contains("user_username"));
    }

    @Test
    public void getRandomPicture_failure() throws Exception {
        // Arrange
        RoundService roundServiceSpy = spy(roundService);
        UUID gameId = UUID.randomUUID();
        Game game = new Game();
        game.setGameId(gameId);
        game.setGuessTime(30);
        Round round = new Round();

        when(gameService.getGame(gameId)).thenReturn(game);

        // Act
        String result = roundServiceSpy.getRandomPicture(round, game);

        // Assert
        verify(roundRepository, times(1)).save(round);
        assertTrue(result.contains("fallback response"));
    }
*/
    @Test
    public void calculateEndTime_success() {
        // Arrange
        Game game = new Game();
        game.setGuessTime(30);

        // Act
        LocalTime result = roundService.calculateEndTime(game);

        // Assert
        LocalTime expectedEndTime = ZonedDateTime.now(ZoneId.of("Europe/Zurich")).toLocalTime().plusSeconds(31);
        assertEquals(expectedEndTime.getHour(), result.getHour());
        assertEquals(expectedEndTime.getMinute(), result.getMinute());
        assertEquals(expectedEndTime.getSecond(), result.getSecond(), 1); // allow some tolerance for test execution delay
    }

    @Test
    public void calculateDistance_success() {
        // Arrange
        double lat1 = 47.399591;
        double lon1 = 8.514325;
        double lat2 = 46.94809;
        double lon2 = 7.44744;

        // Act
        double result = roundService.calculateDistance(lat1, lon1, lat2, lon2);

        // Assert
        double expectedDistance = 94.48; // approximate value in kilometers
        assertEquals(expectedDistance, result, 1.0); // allow 1 km tolerance
    }
}
