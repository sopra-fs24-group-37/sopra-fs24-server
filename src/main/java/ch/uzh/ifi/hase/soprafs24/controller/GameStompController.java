package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;


@Controller
public class GameStompController {
    private final GameService gameService;
    private final RoundService roundService;
    private final WebSocketService webSocketService;

    GameStompController(GameService gameService, RoundService roundService, WebSocketService webSocketService) {
        this.gameService = gameService;
        this.roundService = roundService;
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/games/{gameId}/joining")
    public void getLobbyInformation(@DestinationVariable("gameId") UUID gameId){
        Game game = gameService.getGame(gameId);
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
        webSocketService.sendMessageToSubscribers("/topic/games/" + gameId, gameGetDTO);
    }

    @MessageMapping("/games/{gameId}/leaving")
    public void leaveGame(@PathVariable UUID gameId, @DestinationVariable("gameId") Long userId) {
        Game game = gameService.leaveGame(gameId, userId);
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
        webSocketService.sendMessageToSubscribers("/topic/games/" + gameId, gameGetDTO);
    }

    @MessageMapping("/games/{gameId}/started")
    public void gameStartedInfo(@DestinationVariable("gameId") UUID gameId){
        roundService.createRound(gameId);
        webSocketService.sendMessageToSubscribers("/topic/games/" + gameId +"/started", "Game has started");
    }



}
