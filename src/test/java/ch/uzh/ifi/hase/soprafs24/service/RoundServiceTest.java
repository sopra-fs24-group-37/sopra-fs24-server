package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Round;
import ch.uzh.ifi.hase.soprafs24.entity.RoundStats;
import ch.uzh.ifi.hase.soprafs24.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoundStatsRepository;
import ch.uzh.ifi.hase.soprafs24.config.ApiKeyConfig;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opengis.temporal.Clock;
import org.opengis.temporal.Instant;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;


@SpringBootTest
public class RoundServiceTest {

    @Mock
    private RoundRepository roundRepository;
    @Mock
    private RoundStatsRepository roundStatsRepository;
    @Mock
    private GameService gameService;
    @Mock
    private HttpClient httpClient;
    @Mock
    private ApiKeyConfig apiKeyConfig;

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
    public void generateResponse_success() {
        // Arrange
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("urls", new JSONObject().put("regular", "https://example.com"));
        jsonResponse.put("user", new JSONObject().put("name", "John Doe").put("username", "johndoe"));
        double latitude = 47.399591;
        double longitude = 8.514325;
        LocalTime endTime = LocalTime.now();
    
        // Act
        JSONObject result = roundService.generateResponse(jsonResponse, latitude, longitude, endTime);
    
        // Assert
        assertEquals("https://example.com", result.get("regular_url"));
        assertEquals(47.399591, result.get("latitude"));
        assertEquals(8.514325, result.get("longitude"));
        assertEquals("John Doe", result.get("user_name"));
        assertEquals("johndoe", result.get("user_username"));
        assertEquals(endTime.toString(), result.get("end_time"));
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
    
    @Test
    public void calculateDistance_success() {
        // Arrange
        double lat1 = 47.399591;
        double lon1 = 8.514325;
        double lat2 = 47.399591;
        double lon2 = 8.514325;
    
        // Act
        double result = roundService.calculateDistance(lat1, lon1, lat2, lon2);
    
        // Assert
        assertEquals(0, result);
    }

    @Test
    public void testCalculateEndTime() {
        // Arrange
        Game game = new Game(); 
        game.setGuessTime(5); 
        
        // Act
        LocalTime result = roundService.calculateEndTime(game);

        // Assert
        ZoneId zurichZone = ZoneId.of("Europe/Zurich");
        ZonedDateTime zurichTime = ZonedDateTime.now(zurichZone);
        LocalTime expectedTime = zurichTime.toLocalTime().plusSeconds(6); // guessTime + 1
        long secondsBetween = ChronoUnit.SECONDS.between(expectedTime, result);

        // Assert that the difference is small to accommodate execution time delays
        assertTrue(Math.abs(secondsBetween) < 1);
    }
    
    @Test
    public void generateFallbackResponse_success() {
        // Arrange
        LocalTime endTime = LocalTime.now();
        Round round = new Round();
    
        // Act
        JSONObject result = roundService.generateFallbackResponse(endTime, round);
    
        // Assert
        // The result should contain the expected values
        assertEquals("https://images.unsplash.com/photo-1594754654150-2ae221b25fe8?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w1NzE3ODd8MHwxfHJhbmRvbXx8fHx8fHx8fDE3MTYyMDcwMzl8&ixlib=rb-4.0.3&q=80&w=1080", result.get("regular_url"));
        assertEquals(47.399591, result.get("latitude"));
        assertEquals(8.514325, result.get("longitude"));
        assertEquals(endTime.toString(), result.get("end_time"));
    
        // The round should have been updated with the latitude and longitude
        assertEquals(47.399591, round.getLatitude(), 0.0001);
        assertEquals(8.514325, round.getLongitude(), 0.0001);
    }

}