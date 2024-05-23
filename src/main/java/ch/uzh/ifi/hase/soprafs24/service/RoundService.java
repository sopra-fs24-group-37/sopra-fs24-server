package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.config.ApiKeyConfig;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoundStatsRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoundService {
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private RoundStatsRepository roundStatsRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private ApiKeyConfig apiKeyConfig;


    private static final double EARTH_RADIUS = 6371; // in kilometers

    public void createRound(UUID gameId) {
        // Fetch game
        Game game = gameService.getGame(gameId);

        // Set default values and save the Round instance
        Round newRound = new Round();
        newRound.setGameId(gameId);
        newRound.setCheckIn(0);
        int i = 0;

        // Iterate through gamePlayer set and create RoundStats instances
        for (GamePlayer gamePlayer : game.getPlayers()) {
            RoundStats roundStats = new RoundStats(game, gamePlayer, 0, 0, 0);
            roundStatsRepository.save(roundStats);
            newRound.addExistingRoundStats(roundStats);
        }

        // Save the Round instance again to persist the RoundStats instances
        roundRepository.save(newRound);
        roundRepository.flush();
    }

    public Round getRound(UUID gameId) {
        Optional<Round> roundOpt = roundRepository.findById(gameId);
        if (!roundOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Round not found!");
        }
        return roundOpt.get();
    }

    @Transactional
    public void updatePlayerGuess(UUID gameId, Long userId, Integer pointsInc, double latitude, double longitude) {
        RoundStats roundStats = roundStatsRepository.findByGame_GameIdAndGamePlayer_PlayerId(gameId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found in the game"));

        roundStats.updateRoundStats(pointsInc, latitude, longitude);
        roundStatsRepository.save(roundStats);
    }

    public String getRandomPicture(Round round, Game game) {
        try {
            // Do the random picture Api Call
            JSONObject jsonResponse = fetchPictureFromApi();

            // Check location data of api call
            JSONObject location = jsonResponse.optJSONObject("location");
            if (location == null) {
                return getRandomPicture(round, game);
            }
            double latitude = location.getJSONObject("position").getDouble("latitude");
            double longitude = location.getJSONObject("position").getDouble("longitude");

            // Set objects with data
            round.setLatitude(latitude);
            round.setLongitude(longitude);
            roundRepository.save(round);

            // Return trimmed object to the user
            LocalTime endTime = calculateEndTime(game);
            return generateResponse(jsonResponse, latitude, longitude, endTime).toString();
        } catch (Exception e) {
            System.err.println("An error occurred. Returning fallback response.");
            System.err.println("Error details: " + e.getMessage());
            LocalTime endTime = calculateEndTime(game);
            return generateFallbackResponse(endTime,round).toString();
        }
    }

    private JSONObject fetchPictureFromApi() throws URISyntaxException, IOException, InterruptedException {
        String apiUrl = "https://api.unsplash.com/photos/random";
        String query = "Switzerland+landscape+cityscape";
        String clientId = apiKeyConfig.getCurrentApiKey(); // Replace with your Unsplash access key
        String urlString = apiUrl + "?query=" + query;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .header("Authorization", "Client-ID " + clientId)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }

    public LocalTime calculateEndTime(Game game) {
        int guessTime = game.getGuessTime() + 1;

        // Get the current time in Zurich
        ZoneId zurichZone = ZoneId.of("Europe/Zurich");
        ZonedDateTime zurichTime = ZonedDateTime.now(zurichZone);
        LocalTime generationTime = zurichTime.toLocalTime();

        return generationTime.plusSeconds(guessTime);
    }

    private JSONObject generateFallbackResponse(LocalTime endTime, Round round) {
        double latitude = 47.399591;
        double longitude = 8.514325;

        JSONObject fallbackResponse = new JSONObject();
        fallbackResponse.put("regular_url",
                "https://images.unsplash.com/photo-1594754654150-2ae221b25fe8?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w1NzE3ODd8MHwxfHJhbmRvbXx8fHx8fHx8fDE3MTYyMDcwMzl8&ixlib=rb-4.0.3&q=80&w=1080");
        fallbackResponse.put("user_name", "Raphi See");
        fallbackResponse.put("user_username", "raphisee");
        fallbackResponse.put("latitude", latitude);
        fallbackResponse.put("longitude", longitude);
        fallbackResponse.put("end_time", endTime.toString());

        round.setLatitude(latitude);
        round.setLongitude(longitude);
        roundRepository.save(round);

        return fallbackResponse;
    }

    private JSONObject generateResponse(JSONObject jsonResponse, double latitude, double longitude, LocalTime endTime) {
        JSONObject trimmedResponse = new JSONObject();
        trimmedResponse.put("regular_url", jsonResponse.getJSONObject("urls").getString("regular"));
        trimmedResponse.put("latitude", latitude);
        trimmedResponse.put("longitude", longitude);
        trimmedResponse.put("user_name", jsonResponse.getJSONObject("user").getString("name"));
        trimmedResponse.put("user_username", jsonResponse.getJSONObject("user").getString("username"));
        trimmedResponse.put("end_time", endTime.toString());
        return trimmedResponse;
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Haversine formula
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;

        return distance;
    }

}
