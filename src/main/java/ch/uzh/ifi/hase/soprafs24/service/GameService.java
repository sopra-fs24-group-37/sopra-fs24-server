package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    public Game createGame(String gameMaster) {
        Game newGame = new Game();
        newGame.setGameId(UUID.randomUUID());
        newGame.setGameStatus(GameStatus.WAITING);
        newGame.setGameMaster(gameMaster);
        newGame.setPlayers(gameMaster);
        gameRepository.save(newGame);
        return newGame;
    }

    public Game joinGame(UUID gameId, String username) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (!gameOpt.isPresent()) {
            throw new IllegalArgumentException("Game not found!");
        }

        Game game = gameOpt.get();
        String players = game.getPlayers();

        // Check if the user is already in the game
        if (players != null && Arrays.asList(players.split(",")).contains(username)) {
            throw new IllegalStateException("User already in the game!");
        }

        // Add the player to the game
        if (players == null || players.isEmpty()) {
            game.setPlayers(username);
        } else {
            game.setPlayers(players + "," + username);
        }
        
        gameRepository.save(game);
        return game;
    }

    public Game leaveGame(UUID gameId, String username) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (!gameOpt.isPresent()) {
            throw new IllegalArgumentException("Game not found!");
        }

        Game game = gameOpt.get();
        String players = game.getPlayers();
        String[] playerArray = players.split(",");

        // Check if the user is already in the game
        if (!Arrays.asList(playerArray).contains(username)) {
            throw new IllegalStateException("User not in the game!");
        }

        // Remove the player from the game
        game.setPlayers(String.join(",", Arrays.stream(playerArray)
            .filter(name -> !name.equals(username))
            .toArray(String[]::new)));
        
        gameRepository.save(game);
        return game;
    }
    
}
