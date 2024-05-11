package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.stomp.LeaveGamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

    @MessageMapping("/games/{gameId}/started")
    public void gameStartedInfo(@DestinationVariable("gameId") UUID gameId){
        roundService.createRound(gameId);
        webSocketService.sendMessageToSubscribers("/topic/games/" + gameId +"/started", "Game has started");
    }

    @MessageMapping("/games/{gameId}/settings")
    public void setLobbyInformation(@DestinationVariable("gameId") UUID gameId,
                                    @RequestParam(required = false) Integer numRounds,
                                    @RequestParam(required = false) Integer guessTime,
                                    @RequestParam(required = false) Boolean setGamePassword) {
                                        
        Game game = gameService.getGame(gameId);
        Boolean doUpdate = false;

        // Set numRounds if provided and within the valid range
        if (numRounds != null && numRounds >= 2 && guessTime <= 10) {
            game.setNumRounds(numRounds);
            doUpdate = true;
        }

        // Set guessTime if provided and within the valid range
        if (guessTime != null && guessTime >= 10 && guessTime <= 30) {
            game.setGuessTime(guessTime);
            doUpdate = true;
        }

        // Set gamePassword if required, and make it a a 6-digit integer
        if (setGamePassword == true) {
            int gamePassword = (int) (Math.random() * 900000) + 100000;
            game.setPassword(gamePassword);
            doUpdate = true;
        }

        // Update the changes in the database
        if (doUpdate) {
            gameService.updateGame(game);
        }

        // Convert Game to DTO
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

        // Send lobby information via WebSocket
        webSocketService.sendMessageToSubscribers("/topic/games/" + gameId, gameGetDTO);
    }

}
