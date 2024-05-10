package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import ch.uzh.ifi.hase.soprafs24.entity.Round;
import ch.uzh.ifi.hase.soprafs24.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoundDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GamePlayerService;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class RoundController {
    private final GameService gameService;
    private final GamePlayerService gamePlayerService;
    private final RoundService roundService;

    @Autowired
    private RoundRepository roundRepository;

    RoundController(GameService gameService, GamePlayerService gamePlayerService, RoundService roundService) {
        this.gameService = gameService;
        this.gamePlayerService = gamePlayerService;
        this.roundService = roundService;
    }

    @GetMapping("/round/{gameId}/leaderboard")
    public ResponseEntity<RoundDTO> getLeaderboard(@PathVariable UUID gameId) {
        //Get all the relevant objects
        Round round = roundService.getRound(gameId);

        RoundDTO roundDTO = DTOMapper.INSTANCE.convertEntityToRoundDTO(round);

        return ResponseEntity.ok(roundDTO);
    }

}



//produces this "players":[{"playerId":2,"user":{"userId":1,"username":"andri"},"score":0}]
//now join it with this {"playerId":2"pointsScored":0, "lat": 46.8787,"long": 48.5850}

// To produce this "players":[{"playerId":2,"user":{"userId":1,"username":"andri"},"score":0, "incscore":0, "lat": 46.8787,"long": 48.5850}]
// So it can be returned

// Combine player information from the game with their guesses from the round
        /*
        List<Map<String, Object>> leaderboard = new ArrayList<>();
        for (GamePlayer gamePlayer : game.getPlayers()) {
            long playerId = gamePlayer.getPlayerId();
            Map<String, Object> playerInfo = new HashMap<>();
            playerInfo.put("playerId", playerId);
            // Add other player information as needed
            playerInfo.put("userId", gamePlayer.getUser().getUserId());
            playerInfo.put("username", gamePlayer.getUser().getUsername());
            playerInfo.put("score", gamePlayer.getScore());


            Map<Long, Double[]> playerGuess = round.getGuesses();
            if (playerGuess.containsKey(playerId)) {
                Double[] guess = playerGuess.get(playerId);
                playerInfo.put("lat", guess[0]);
                playerInfo.put("long", guess[1]);
            } else {
                playerInfo.put("lat", null);
                playerInfo.put("long", null);
            }

            leaderboard.add(playerInfo);
           }
         */
