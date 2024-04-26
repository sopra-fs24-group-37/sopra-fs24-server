package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GamePlayerRepository;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;



    public List<Game> getGames() {
        return gameRepository.findAll();
      }
  
    public Game getGame(UUID gameId) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (!gameOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found!");
        }
        return gameOpt.get();
    }

    @Transactional
    public void updatePlayerScore(UUID gameId, Long userId, Integer score) {
        GamePlayer gamePlayer = gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Player not found in the game"));

        gamePlayer.setScore(gamePlayer.getScore() + score);
        gamePlayerRepository.save(gamePlayer);

        User user = gamePlayer.getUser();
        user.setTotalScores(user.getTotalScores() + score);
        
        userRepository.save(user);
    }
    
    public Game createGame(Long userId) { // userId of gameMaster
        User gameMaster = userService.findUserbyId(userId);
        Game newGame = new Game();
        newGame.setGameId(UUID.randomUUID());
        newGame.setGameStatus(GameStatus.WAITING);
        newGame.setGameMaster(userId);
        newGame.addNewPlayer(gameMaster);
        // newGame.setPlayers(new ArrayList<>(Arrays.asList(gameMaster)));
        newGame = gameRepository.save(newGame);
        gameRepository.flush();
        return newGame;
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

    public Game endGame(UUID gameId) {
        Game game = getGame(gameId);

        if (game.getGameStatus() == GameStatus.ENDED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game already ended!");
        }
    
        game.setGameStatus(GameStatus.ENDED);
        gameRepository.save(game);
        return game;
    }
    
    public Game joinGame(UUID gameId, Long userId) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        User user = userService.findUserbyId(userId);

        if (!gameOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found!");
        }

        Game game = gameOpt.get();

        // Check if the user is already in the game using a more reliable method
        boolean isAlreadyInGame = game.getPlayers().stream()
            .anyMatch(gp -> gp.getUser().equals(user));
        if (isAlreadyInGame) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already in the game!");
        }

        // Add the player to the game
        game.addNewPlayer(user);

        // Save the updated game
        gameRepository.save(game);
        return game;
    }

    public Game leaveGame(UUID gameId, Long userId) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        User user = userService.findUserbyId(userId);

        if (!gameOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found!");
        }

        Game game = gameOpt.get();

        Optional<GamePlayer> gamePlayerOptional = game.getPlayers().stream()
            .filter(gp -> gp.getUser().equals(user)).findFirst();

        if (!gamePlayerOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not in the game!");
        }

        // Remove the player from the game
        GamePlayer gamePlayer = gamePlayerOptional.get();
        game.getPlayers().remove(gamePlayer);
        gamePlayerRepository.delete(gamePlayer); 
        
        gameRepository.save(game);
        return game;
    } 
}
