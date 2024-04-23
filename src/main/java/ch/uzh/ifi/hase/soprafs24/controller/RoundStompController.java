package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.*;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @MessageMapping("/games/{gameId}/spiedObject")
    public void handleSpiedObject(SpiedObjectIn spiedObjectIn, @DestinationVariable("gameId") int gameId) {
        //extract information from JSON
        String keyword = spiedObjectIn.getObject();
        String color = spiedObjectIn.getColor();
        Location location = spiedObjectIn.getLocation();
        logger.info("Received spiedObject with keyword {} for game {}", keyword, gameId);

        //save information of spied object
        RoundService.saveSpiedObjectInfo(gameId, keyword);

        // Save time as start time of the round
        Date startTime = RoundService.initializeStartTime(gameId);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTimeString = dateFormat.format(startTime);

        float duration = RoundService.getDuration(gameId); //duration in minutes

        //return SpiedObjectOut to subscribers
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/spiedObject", new SpiedObjectOut(location, color, startTimeString, duration));
        RoundService.runTimer(this, gameId);
    }

    public void handleEndRound(int gameId, String message, int amountOfRounds, int currentRound) {
        // send end round message to subscribers
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/endRound", new EndRoundMessage(message, amountOfRounds, currentRound));
    }

    @MessageMapping("/games/{gameId}/nextRound")
    public void nextRound(@DestinationVariable("gameId") int gameId) {
        RoundService.nextRound(gameId);
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/nextRound", new EndRoundMessage("nextRound", 0,0)); // TODO don't rly need to return anything...
    }

    @MessageMapping("/games/{gameId}/guesses")
    public void handleGuess(GuessIn guessIn, @DestinationVariable("gameId") int gameId) {
        logger.info("Handling guess '{}'", guessIn);
        Date guessTime = new Date(); // guess time is registered at request to evaluate how many points the player gets

        //extract information from JSON
        String guess = guessIn.getGuess();
        Player player = playerService.getPlayer(guessIn.getId());

        List<Guess> playerGuesses = RoundService.checkGuessAndAllocatePoints(gameId, player, guess, guessTime);

        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/guesses", playerGuesses);

        RoundService.endRoundIfAllPlayersGuessedCorrectly(this, gameId);
    }

    @MessageMapping("/games/{gameId}/hints")
    public void distributeHints(Hint hint, @DestinationVariable("gameId") int gameId){
        //send hint directly to all subscribers
        logger.info("Distributing hint '{}' for game {}", hint, gameId);
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/hints", hint);
    }

    @MessageMapping("/games/{gameId}/gameOver")
    public void endGame(@DestinationVariable("gameId") int gameId){
        RoundService.handleGameOver(gameId, true);
        GameService.deleteLobby(gameId, playerService);
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/gameOver", new EndRoundMessage("endGame", 0, 0));
    }

    @MessageMapping("/games/{gameId}/playAgain")
    public void playAgain(@DestinationVariable("gameId") int gameId){
        RoundService.handleGameOver(gameId, true);
        Lobby lobby = GameService.getLobby(gameId);
        List<Player> players = new ArrayList<>();
        List<Player> playersOld = lobby.getPlayers();
        for(Player p : playersOld){
            players.add(playerService.getPlayer(p.getId()));
        }
        lobby.resetGameToNull(players);
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/playAgain", new EndRoundMessage("playAgain", 0, 0));
        // wait for a second to make sure the players are in the lobby
        try {
            Thread.sleep(500);
            // send a message over websocket to notify the other players who is in the lobby
            String destination = "/topic/lobbies/" + gameId + "/joined";
            Lobby l = GameService.getLobby(gameId);
            LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertLobbyToLobbyGetDTO(l);
            messagingTemplate.convertAndSend(destination, lobbyGetDTO);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
