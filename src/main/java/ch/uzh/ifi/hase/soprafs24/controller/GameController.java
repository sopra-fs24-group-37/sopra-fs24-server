package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping
    public ResponseEntity<Game> createGame() {
        Game newGame = gameService.createGame();
        return ResponseEntity.status(HttpStatus.CREATED).body(newGame);
    }

    @PutMapping("/{gameId}/join")
    public ResponseEntity<Game> joinGame(@PathVariable UUID gameId, @RequestBody String username) {
        Game game = gameService.joinGame(gameId, username);
        return ResponseEntity.ok().body(game);
    }

    @PutMapping("/{gameId}/leave")
    public ResponseEntity<Game> leaveGame(@PathVariable UUID gameId, @RequestBody String username) {
        Game game = gameService.leaveGame(gameId, username);
        return ResponseEntity.ok().body(game);
    }
}
