package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
        newGame = gameRepository.save(newGame);
        gameRepository.flush();
        return newGame;
    }

    public Game getGame(UUID gameId) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (!gameOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found!");
        }
        return gameOpt.get();
    }

    public Game startGame(UUID gameId) {
        Game game = getGame(gameId);

        if (game.getGameStatus() == GameStatus.STARTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game already started!");
        }

        if (game.getGameStatus() == GameStatus.ENDED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game already ended!");
        }
        
        // This would never be called in theory, but just in case
        // some other status bug occurs or we increase the number/types of status
        if (game.getGameStatus() != GameStatus.WAITING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game is not in WAITING status!");
        }
    
        int numberOfPlayers = game.getPlayers().size();
        if (numberOfPlayers < 2 || numberOfPlayers > 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game must have between 2 and 4 players to start!");
        }
    
        game.setGameStatus(GameStatus.STARTED);
        gameRepository.save(game);
        return game;
    }
    
    public Game joinGame(UUID gameId, String username) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (!gameOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found!");
        }

        Game game = gameOpt.get();
        List<String> players = game.getPlayers();

        // Check if the user is already in the game
        if (players != null && players.contains(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already in the game!");
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found!");
        }

        Game game = gameOpt.get();
        List<String> players = game.getPlayers();

        // Check if the user is in the game
        if (!players.contains(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not in the game!");
        }

        // Remove the player from the game
        players.remove(username);
        game.setPlayers(players);
        
        gameRepository.save(game);
        return game;
    } 
}
