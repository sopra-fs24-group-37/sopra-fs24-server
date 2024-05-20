package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePlayerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private RoundService roundService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getAllGames() {
      List<Game> games = gameService.getGames();
      List<GameGetDTO> gameGetDTOs = new ArrayList<>();

      for (Game game : games) {
        gameGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameGetDTO(game));
      }
      return gameGetDTOs;
    }
  
    @GetMapping("/{gameId}")
    public GameGetDTO getGameById(@PathVariable UUID gameId) {
      Game game = gameService.getGame(gameId);
      GameGetDTO gameGetDTO =DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
      return gameGetDTO;
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<?> deleteGame(@PathVariable UUID gameId) {
      gameService.deleteGameById(gameId);
      return ResponseEntity.status(HttpStatus.OK).body("Game Deleted");
    }

    @PostMapping
    public ResponseEntity<GameGetDTO> createGame(@RequestBody GamePostDTO gamePostDTO) {
      Game newGame = gameService.createGame(gamePostDTO.getGameMaster());
      GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(newGame);
      return ResponseEntity.status(HttpStatus.CREATED).body(gameGetDTO);
    }

    @PutMapping("/{gameId}/start")
    public ResponseEntity<GameGetDTO> startGame(@PathVariable UUID gameId) {
      Game game = gameService.startGame(gameId);
      roundService.createRound(gameId);
      GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
      return ResponseEntity.ok().body(gameGetDTO);
    }

    @PutMapping("/{gameId}/end")
    public ResponseEntity<GameGetDTO> endGame(@PathVariable UUID gameId) {
      Game game = gameService.endGame(gameId);
      GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
      return ResponseEntity.ok().body(gameGetDTO);
    }

    @PutMapping("/{gameId}/join")
    public ResponseEntity<Game> joinGame(@PathVariable UUID gameId, @RequestBody Long userId, @RequestParam(required = false) Integer gamePassword) {
        Game game = gameService.joinGame(gameId, userId, gamePassword);
        return ResponseEntity.ok().body(game);
    }    
  
    @PutMapping("/{gameId}/leave")
    public ResponseEntity<Game> leaveGame(@PathVariable UUID gameId, @RequestBody Long userId) {
      Game game = gameService.leaveGame(gameId, userId);
      // if (game.getPlayers().isEmpty()) {
      //   gameService.deleteGameById(gameId);
      //   return ResponseEntity.ok().build();
      // }
      return ResponseEntity.ok().body(game);
    }

    @GetMapping("/{gameId}/leaderboard")
    public ResponseEntity<LeaderboardDTO> getLeaderboard(@PathVariable UUID gameId) {
      Game game = gameService.calculateLeaderboard(gameId);

      // Convert to GameGetDTO using DTO Mapper
      LeaderboardDTO leaderboardDTO = DTOMapper.INSTANCE.convertEntityToLeaderboardDTO(game);

      // Since LeaderboardDTO extends GameGetDTO, we need to set winners separately
      if (game.getPlayers() != null && !game.getPlayers().isEmpty()) {
        Set<GamePlayerDTO> winners = game.getPlayers().stream()
            .map(DTOMapper.INSTANCE::convertEntityToGamePlayerDTO)
            .limit(3)  // Only take the top 3 or fewer
            .collect(Collectors.toCollection(LinkedHashSet::new));

          leaderboardDTO.setWinners(winners);
        } else {
          leaderboardDTO.setWinners(new LinkedHashSet<>());  // Ensure winners is never null
        }

      return ResponseEntity.ok(leaderboardDTO);
    }
}
