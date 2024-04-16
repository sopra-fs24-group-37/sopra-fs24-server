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
        newGame.setPlayers(new ArrayList<>(Arrays.asList(gameMaster)));
        gameRepository.save(newGame);
        return newGame;
    }

    public Game joinGame(UUID gameId, String username) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (!gameOpt.isPresent()) {
            throw new IllegalArgumentException("Game not found!");
        }

        Game game = gameOpt.get();
        List<String> players = game.getPlayers();

        // Check if the user is already in the game
        if (players != null && players.contains(username)) {
            throw new IllegalStateException("User already in the game!");
        }

        // Add the player to the game
        players.add(username);
        game.setPlayers(players);
        
        gameRepository.save(game);
        return game;
    }

    public Game leaveGame(UUID gameId, String username) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (!gameOpt.isPresent()) {
            throw new IllegalArgumentException("Game not found!");
        }

        Game game = gameOpt.get();
        List<String> players = game.getPlayers();

        // Check if the user is in the game
        if (!players.contains(username)) {
            throw new IllegalStateException("User not in the game!");
        }

        // Remove the player from the game
        players.remove(username);
        game.setPlayers(players);
        
        gameRepository.save(game);
        return game;
    } 
}
