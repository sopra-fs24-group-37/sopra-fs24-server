package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePutDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    // Implement GET endpoints for Games here... Not a priority atm but will need it very soon lol

    // @GetMapping
    // @ResponseStatus(HttpStatus.OK)
    // @ResponseBody
    // public List<GameGetDTO> getAllUsers() {
    //   // fetch all users in the internal representation
    //   List<Game> users = gameService.getGames();
    //   List<GameGetDTO> gameGetDTOs = new ArrayList<>();
  
    //   // convert each user to the API representation
    //   for (Game game : games) {
    //     gameGetDTOs.add(DTOMapper.INSTANCE.convertGameToGameGetDTO(game));
    //   }
    //   return gameGetDTOs;
    // }
  
    // @GetMapping("/users/{userId}")
    // public UserGetDTO getUserById(@PathVariable Long userId) {
  
    //   User user = userService.findUserbyId(userId);
  
    //   UserGetDTO userGetDTO =DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  
    //   return userGetDTO;
    // }

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody String gameMaster) {
        Game newGame = gameService.createGame(gameMaster);
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
