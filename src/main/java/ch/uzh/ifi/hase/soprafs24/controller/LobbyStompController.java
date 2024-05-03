package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LobbyStompController {

    private final UserService userService;
    private final GameService gameService;
    private final WebSocketService webSocketService;

    LobbyStompController(GameService gameService, WebSocketService webSocketService, UserService userService) {
        this.gameService = gameService;
        this.webSocketService = webSocketService;
        this.userService = userService;
    }

    @MessageMapping("/users/updateUsers")
    public void updateUsers(){
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        webSocketService.sendMessageToSubscribers("/topic/users/getUsers", userGetDTOs);
    }
    @MessageMapping("/games/updateGames")
    public void updateGames(){
        List<Game> games = gameService.getGames();
        List<GameGetDTO> gameGetDTOs = new ArrayList<>();

        for (Game game : games) {
            gameGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameGetDTO(game));
        }
        webSocketService.sendMessageToSubscribers("/topic/games/getGames", gameGetDTOs);
    }
}
