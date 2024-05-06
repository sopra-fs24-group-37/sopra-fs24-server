package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Round;
import ch.uzh.ifi.hase.soprafs24.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.stomp.GuessPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.*;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;


@Controller
public class RoundStompController {
    private final RoundService roundService;
    private final WebSocketService webSocketService;
    private final GameService gameService;

    @Autowired
    private RoundRepository roundRepository;

    RoundStompController(RoundService roundService, WebSocketService ws, GameService gameService) {
        this.roundService = roundService;
        this.webSocketService = ws;
        this.gameService = gameService;
    }

    @MessageMapping("/games/{gameId}/checkin")
    public void getNewRound(@DestinationVariable("gameId") UUID gameId){
        Round round = roundService.getRound(gameId);
        round.incCheckIn();
        System.out.println(round.getCheckIn());
        roundRepository.save(round);
        if (round.getRoundsPlayed()>=2){
            webSocketService.sendMessageToSubscribers("/topic/games/" + gameId +"/ended", "Game will end after this turn");
        }
        if(round.getCheckIn()>=2){
            String id = roundService.getRandomPicture(round);
            webSocketService.sendMessageToSubscribers("/topic/games/" + gameId +"/round", id);
            round.clearCheckIn();
            round.incRoundsPlayed();
            roundRepository.save(round);
            if(round.getRoundsPlayed()>=3){
                // Update User Statistics on User Profiles
                gameService.updateUserStatistics(gameId);

                // Mark Game Status as Ended
                gameService.endGame(gameId);
            }
        }
    }

    @MessageMapping("/games/{gameId}/guess")
    public void getGuesses(GuessPostDTO guess, @DestinationVariable("gameId") UUID gameId){
        double lat = guess.getLat();
        double lng = guess.getLng();
        Long userId = guess.getUserId();
        System.out.println("UserId Nr " + userId + " has guessed in game " + gameId + " with guess lat " + lat + " and lng " + lng);
        Round round = roundService.getRound(gameId);
        double correctLat = round.getLatitude();
        double correctLng = round.getLongitude();
        int distance = (int) roundService.calculateDistance(correctLat,correctLng,lat,lng);
        System.out.println("UserId Nr " + userId + " has guessed in game " + gameId + " the distance is " + distance);
        if(distance<=100) {
            gameService.updatePlayerScore(gameId, userId, 100 - distance);
        }
        else{
            System.out.println("Too bad too far away");
        }
    }
}

