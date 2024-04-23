package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;

import java.util.UUID;


@Controller
public class RoundStompController {
    private final GameService gameService;
    private final UserService userService;
    private final WebSocketService webSocketService;

    RoundStompController(GameService gameService, UserService userService, WebSocketService webSocketService) {
        this.gameService = gameService;
        this.userService = userService;
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/games/{gameId}/{roundNr}")
    public void getLobbyInformation(@DestinationVariable("gameId") UUID gameId, @DestinationVariable("roundNr") int roundNr){
        //round
        //webSocketService.sendMessageToSubscribers("/topic/games/" + gameId, round);
    }


}
