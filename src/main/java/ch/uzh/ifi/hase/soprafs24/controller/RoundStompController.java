package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Round;
import ch.uzh.ifi.hase.soprafs24.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.stomp.GuessPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.*;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;


@Controller
public class RoundStompController {
    private final Logger logger = LoggerFactory.getLogger(GameStompController.class);
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
        roundRepository.save(round);
        if (round.getRoundsPlayed()>=3){
            webSocketService.sendMessageToSubscribers("/topic/games/" + gameId +"/ended", "Game has ended");
        }
        else if(round.getCheckIn()>=2){
            String id = roundService.getRandomPicture(round);
            webSocketService.sendMessageToSubscribers("/topic/games/" + gameId +"/round", id);
            round.clearCheckIn();
            round.incRoundsPlayed();
            roundRepository.save(round);
        }
    }

    @MessageMapping("/games/{gameId}/guess")
    public void sendGuesses(GuessPostDTO guess, @DestinationVariable("gameId") UUID gameId){
        double lat = guess.getLat();
        double lng = guess.getLng();
        Long userId = guess.getUserId();
        System.out.println("UserId Nr" + userId + "has guessed in game" + gameId);
        Round round = roundService.getRound(gameId);
        double correctLat = round.getLatitude();
        double correctLng = round.getLongitude();
        int distance = (int) roundService.calculateDistance(correctLat,correctLng,lat,lng);
        gameService.updatePlayerScore(gameId, userId, distance);
    }
}
