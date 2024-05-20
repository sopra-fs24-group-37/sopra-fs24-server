package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GamePlayerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
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

    public void updateGame(Game game) {
        gameRepository.saveAndFlush(game);
    }

    public Game findGamebyId(UUID gameId) {
        Game gameById = this.gameRepository.findById(gameId).orElse(null);
        if (gameById == null) {
          throw new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Game with user ID " + gameId + " was not found!");
        }
        return gameById;
      }

    public void deleteGameById(UUID gameId) {
        findGamebyId(gameId); // To throw exception if user does not exist
        this.gameRepository.deleteById(gameId);
      }

    public int getNumPlayers(UUID gameId) {
        Game game = getGame(gameId);
        Set<GamePlayer> players = game.getPlayers();
        return players.size();
    }

    @Transactional(readOnly = true)
    public Game calculateLeaderboard(UUID gameId) {
        Game game = getGame(gameId);

        if (game != null && game.getPlayers() != null) {
            // Sort players by score
            List<GamePlayer> sortedPlayers = new ArrayList<>(game.getPlayers());
            sortedPlayers.sort(Comparator.comparingInt(GamePlayer::getScore).reversed());

            // Just return sorted game
            game.setPlayers(new LinkedHashSet<>(sortedPlayers));
        }

        return game;
    }

    @Transactional
    public void updateUserStatistics(UUID gameId) {
        Game game = getGame(gameId);
        Set<GamePlayer> players = game.getPlayers();

        if (game != null && players != null) {
            for(GamePlayer player : players) {
                User user = player.getUser();

                // Update User's Total Scores after last game round finished
                user.setTotalScores(player.getScore());

                // Update User's Total Wins after last game round finished
                Game gameLeaderboard = calculateLeaderboard(gameId);
                Set<GamePlayer> playersLeaderboard = gameLeaderboard.getPlayers();
                GamePlayer winnerPlayer = playersLeaderboard.stream().findFirst().orElse(null);

                if (winnerPlayer != null) {
                    User winner = winnerPlayer.getUser();

                    if (user == winner) {
                        user.setGamesWon(user.getGamesWon() + 1);
                    }
                }

                // Update User's Total Games Played after last round finished
                user.setGamesPlayed(user.getGamesPlayed() + 1);

                userRepository.save(user);
            }
        }
    }

    @Transactional
    public Boolean useDoubleScorePowerUp(UUID gameId, Long userId) {
        /*
         * Returns True if GamePlayer is able to use their Double Score Power up and sets it to used in the DB.
         * Otherwise it returns false.
         */
        GamePlayer gamePlayer = gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Player not found in the game"));

        if (gamePlayer.getDoubleScore() == true) {
            gamePlayer.setDoubleScore(false);
            gamePlayerRepository.save(gamePlayer);
            return true;
        }

        return false;
    }

    @Transactional
    public Boolean useCantonHintPowerUp(UUID gameId, Long userId) {
        /*
         * Returns True if GamePlayer is able to use their Canton Hint Power up and sets it to used in the DB.
         * Otherwise it returns false.
         */
        GamePlayer gamePlayer = gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Player not found in the game"));

        if (gamePlayer.getCantonHint() == true) {
            gamePlayer.setCantonHint(false);
            gamePlayerRepository.save(gamePlayer);
            return true;
        }
        return false;
    }

    @Transactional
    public Boolean useMultipleCantonHintPowerUp(UUID gameId, Long userId) {
        /*
         * Returns True if GamePlayer is able to use their Multiple Canton Hint Power up and sets it to used in the DB.
         * Otherwise it returns false.
         */
        GamePlayer gamePlayer = gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Player not found in the game"));

        if (gamePlayer.getMultipleCantonHint() == true) {
            gamePlayer.setMultipleCantonHint(false);
            gamePlayerRepository.save(gamePlayer);
            return true;
        }
        return false;
    }

    @Transactional
    public void updatePlayerScore(UUID gameId, Long userId, Integer score) {
        GamePlayer gamePlayer = gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Player not found in the game"));

        gamePlayer.setScore(gamePlayer.getScore() + score);
        gamePlayerRepository.save(gamePlayer);
    }
    
    public Game createGame(Long userId) { // userId of gameMaster
        User gameMaster = userService.findUserbyId(userId);
        Game newGame = new Game();
        newGame.setGameId(UUID.randomUUID());
        newGame.setGameStatus(GameStatus.WAITING);
        newGame.setGameMaster(userId);
        newGame.addNewPlayer(gameMaster);
        newGame.setNumRounds(3);  // By default set Number of Rounds to 3
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

    public Game joinGame(UUID gameId, Long userId, Integer gamePassword) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        User user = userService.findUserbyId(userId);
    
        if (!gameOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found!");
        }

        Game game = gameOpt.get();
    
        // Check if the game has a password set
        if (game.getPassword() != null) {
            // If a password is provided but the game does not have one set, or the provided password is incorrect, return an error
            if (gamePassword == null || !game.getPassword().equals(gamePassword)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password!");
            }
        } else {
            // If a password is provided but the game does not have one set, return an error
            if (gamePassword != null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This game has no password!");
            }
        }
    
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
