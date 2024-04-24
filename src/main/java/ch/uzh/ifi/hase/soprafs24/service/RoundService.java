package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Round;
import ch.uzh.ifi.hase.soprafs24.repository.RoundRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoundService {
    @Autowired
    private RoundRepository roundRepository;
    private static final double EARTH_RADIUS = 6371; // in kilometers

    public Round createRound(UUID gameId) {
        Round newRound = new Round();
        newRound.setGameId(gameId);
        newRound.setCheckIn(0);
        newRound = roundRepository.save(newRound);
        roundRepository.flush();
        return newRound;
    }

    public Round getRound(UUID gameId) {
        Optional<Round> roundOpt = roundRepository.findById(gameId);
        if (!roundOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Round not found!");
        }
        return roundOpt.get();
    }

    public String getRandomPicture(Round round) {
        try {
            // Construct URL
            String apiUrl = "https://api.unsplash.com/photos/random";
            String query = "Switzerland+landscape+cityscape";
            String clientId = "Ri4Er_Nr9GAMk_QTpErZvHuVESEVmM7RNKnymwEcNZM"; // Replace with your Unsplash access key

            String urlString = apiUrl + "?query=" + query;

            // Create HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .header("Authorization", "Client-ID " + clientId)
                    .build();

            // Send request and get response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.body());

            // Extract ID
            String id = jsonResponse.getString("id");

            // Get the location object from the JSON response
            JSONObject location = jsonResponse.getJSONObject("location");

            // Get the latitude and longitude from the position object
            double latitude = location.getJSONObject("position").getDouble("latitude");
            double longitude = location.getJSONObject("position").getDouble("longitude");

            round.setLatitude(latitude);
            round.setLongitude(longitude);
            round.setPictureId(id);
            roundRepository.save(round);

            return (id);

        }
        catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return("Ch88hWaANKs");//this is a fallback picture in case something breaks
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
