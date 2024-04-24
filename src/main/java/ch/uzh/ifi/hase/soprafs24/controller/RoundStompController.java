package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Round;
import ch.uzh.ifi.hase.soprafs24.repository.RoundRepository;
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


    @Autowired
    private RoundRepository roundRepository;

    RoundStompController(RoundService roundService, WebSocketService ws) {
        this.roundService = roundService;
        this.webSocketService = ws;
    }

    @MessageMapping("/games/{gameId}/checkin")
    public void getNewRound(@DestinationVariable("gameId") UUID gameId){
        Round round = roundService.getRound(gameId);
        round.incCheckIn();
        roundRepository.save(round);
        int CheckinNum = round.getCheckIn();
        if(CheckinNum>=2){
            String id = roundService.getRandomPicture();
            webSocketService.sendMessageToSubscribers("/topic/games/" + gameId +"/round", id);
            round.clearCheckIn();
            roundRepository.save(round);
        }
    }
}
