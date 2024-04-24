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

    public String getRandomPicture() {
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

            // Print ID
            return (id);

        }
        catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return("Ch88hWaANKs");//this is a fallback picture in case something breaks
    }
}
