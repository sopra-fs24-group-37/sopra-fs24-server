package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import ch.uzh.ifi.hase.soprafs24.rest.dto.stomp.GameSettingsPostDTO;
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
        webSocketService.sendMessageToSubscribers("/topic/games/" + gameId +"/started", "Game has started");
    }

    @MessageMapping("/games/{gameId}/settings")
    public void setLobbyInformation(GameSettingsPostDTO gameSettings, @DestinationVariable("gameId") UUID gameId) {
        Integer numRounds = gameSettings.getNumRounds();
        Integer guessTime = gameSettings.getGuessTime();
        Boolean setGamePassword = gameSettings.getSetGamePassword();
        System.out.println("Game Settings for game with ID " + gameId + ": numRounds " + numRounds + ", guessTime " + guessTime + ", setGamePassword " + setGamePassword);
                                        
        Game game = gameService.getGame(gameId);
        Boolean doUpdate = false;

        // Set numRounds if provided and within the valid range
        if (numRounds != null && game.getNumRounds() != numRounds && numRounds >= 2 && numRounds <= 10) {
            game.setNumRounds(numRounds);
            doUpdate = true;
        }

        // Set guessTime if provided and within the valid range
        if (guessTime != null && game.getGuessTime() != guessTime && guessTime >= 10 && guessTime <= 30) {
            game.setGuessTime(guessTime);
            doUpdate = true;
        }

        // Set gamePassword if required, and make it a a 6-digit integer
        if (setGamePassword == true && (game.getPassword() == null || game.getPassword() == 0)) {
            int gamePassword = (int) (Math.random() * 900000) + 100000;
            game.setPassword(gamePassword);
            doUpdate = true;
        }

        // Set gamePassword if required, and make it a a 6-digit integer
        else if (setGamePassword == false && game.getPassword() != null && game.getPassword() != 0) {
            game.setPassword(null);
            doUpdate = true;
        }

        // Update the changes in the database
        if (doUpdate) {
            gameService.updateGame(game);
        }

        // Convert Game to DTO
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

        System.out.println("\nUpdated Game Password: " + game.getPassword() + " numRounds: " + game.getNumRounds() + " guessTime: " + game.getGuessTime());        

        // Send lobby information via WebSocket
        webSocketService.sendMessageToSubscribers("/topic/games/" + gameId, gameGetDTO);
    }
}
