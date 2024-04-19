package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    // Implement GET endpoints for Games here... Not a priority atm but will need it very soon lol

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getAllUsers() {
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

    @PostMapping
    public ResponseEntity<GameGetDTO> createGame(@RequestBody GamePostDTO gamePostDTO) {
        Game newGame = gameService.createGame(gamePostDTO.getGameMaster());
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(newGame);
        return ResponseEntity.status(HttpStatus.CREATED).body(gameGetDTO);
    }

    @PutMapping("/{gameId}/start")
    public ResponseEntity<GameGetDTO> startGame(@PathVariable UUID gameId) {
        Game game = gameService.startGame(gameId);
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
        return ResponseEntity.ok().body(gameGetDTO);
    }

    @PutMapping("/{gameId}/join")
    public ResponseEntity<Game> joinGame(@PathVariable UUID gameId, @RequestBody Long userId) {
        Game game = gameService.joinGame(gameId, userId);
        return ResponseEntity.ok().body(game);
    }

    @PutMapping("/{gameId}/leave")
    public ResponseEntity<Game> leaveGame(@PathVariable UUID gameId, @RequestBody Long userId) {
        Game game = gameService.leaveGame(gameId, userId);
        return ResponseEntity.ok().body(game);
    }
}
