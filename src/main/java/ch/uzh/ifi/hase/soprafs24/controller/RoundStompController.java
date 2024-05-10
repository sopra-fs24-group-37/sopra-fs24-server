package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import ch.uzh.ifi.hase.soprafs24.entity.Round;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.stomp.GuessPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.*;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;


import java.util.UUID;


@Controller
public class RoundStompController {
    private final RoundService roundService;
    private final WebSocketService webSocketService;
    private final GameService gameService;
    private final GamePlayerService gamePlayerService;

    @Autowired
    private RoundRepository roundRepository;

    RoundStompController(RoundService roundService, WebSocketService ws, GameService gameService, GamePlayerService gamePlayerService) {
        this.roundService = roundService;
        this.webSocketService = ws;
        this.gameService = gameService;
        this.gamePlayerService = gamePlayerService;
    }

    @MessageMapping("/games/{gameId}/checkin")
    public void getNewRound(@DestinationVariable("gameId") UUID gameId){
        Round round = roundService.getRound(gameId);
        round.incCheckIn();
        System.out.println(round.getCheckIn());
        roundRepository.save(round);
        if(round.getCheckIn()>=gameService.getNumPlayers(gameId)){
            if (round.getRoundsPlayed()>=2){
                webSocketService.sendMessageToSubscribers("/topic/games/" + gameId +"/ended", "Game will end after this turn");
            }
            String RoundData = roundService.getRandomPicture(round);
            webSocketService.sendMessageToSubscriberswithoutLog("/topic/games/" + gameId +"/round", RoundData);
            round.clearCheckIn();
            round.incRoundsPlayed();
            roundRepository.save(round);

            //THIS NEEDS TO GO SOMEWHERE ELSE
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
        //Extract all the Data out of the Packet that was sent
        double lat = guess.getLat();
        double lng = guess.getLng();
        Long userId = guess.getUserId();

        //Fetch relevant objects
        GamePlayer gamePlayer = gamePlayerService.getGameplayer(gameId,userId);
        Long gamePlayerId = gamePlayer.getPlayerId();
        Round round = roundService.getRound(gameId);
        User user = gamePlayer.getUser();

        //Extract data out of the objects
        double correctLat = round.getLatitude();
        double correctLng = round.getLongitude();
        int currentScore = gamePlayer.getScore();
        String username = user.getUsername();

        //Calculate score
        int distance = (int) roundService.calculateDistance(correctLat,correctLng,lat,lng);

        //Debug statements
        System.out.println("UserId Nr " + userId + " has guessed in game " + gameId + " with guess lat " + lat + " and lng " + lng);
        System.out.println("UserId Nr " + userId + " has guessed in game " + gameId + " the distance is " + distance);

        //Updating objects
        if(distance<=100) {
            int points = 100 - distance;
            gameService.updatePlayerScore(gameId, userId, points);
            round.updateRoundStats(gamePlayerId, points, currentScore + points, lat, lng);
            roundRepository.save(round);
        }
        else{
            round.updateRoundStats(gamePlayerId,0, currentScore, lat, lng);
            roundRepository.save(round);
        }
    }
}

