package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.service.*;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;


@Controller
public class RoundStompController {
    private final Logger logger = LoggerFactory.getLogger(GameStompController.class);
    private final UserService userService;
    private final RoundService roundService;
    private final GameService gameService;
    private final WebSocketService webSocketService;
    private final SimpMessagingTemplate messagingTemplate;

    RoundStompController(UserService userService, RoundService roundService, GameService gameService, WebSocketService ws, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.roundService = roundService;
        this.gameService = gameService;
        this.webSocketService = ws;
        this.messagingTemplate = messagingTemplate;
    }

    int i = 0;
    @MessageMapping("/games/{gameId}/round")
    public void getLobbyInformation(@DestinationVariable("gameId") UUID gameId){
        i++;
        if(i>=2){
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
                System.out.println("ID: " + id);
                webSocketService.sendMessageToSubscribers("/topic/games/" + gameId +"/round", id);
                i = 0;
            } catch (URISyntaxException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
