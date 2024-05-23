package ch.uzh.ifi.hase.soprafs24.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import ch.uzh.ifi.hase.soprafs24.repository.GamePlayerRepository;


@SpringBootTest
@Transactional  // This will ensure that each test is rolled back after execution
public class GameServiceIntegrationTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Test
    public void testCreateGame() {
        // Create a user first
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPass");
        userService.createUser(user);

        // Use the created user's ID to create a game
        Game newGame = gameService.createGame(user.getUserId());

        assertNotNull(newGame.getGameId());
        assertEquals(GameStatus.WAITING, newGame.getGameStatus());
        assertTrue(newGame.getPlayers().stream().anyMatch(player -> player.getUser().getUserId().equals(user.getUserId())));
    }

    @Test
    public void testStartGame() {
        // Create the first user who will be the game master
        User user1 = new User();
        user1.setUsername("masterUser");
        user1.setPassword("masterPass");
        userService.createUser(user1);
    
        // Create the second user who will join the game
        User user2 = new User();
        user2.setUsername("playerUser");
        user2.setPassword("playerPass");
        userService.createUser(user2);
    
        // Create the game with the first user as the master
        Game game = gameService.createGame(user1.getUserId());
    
        // Have the second user join the game
        game = gameService.joinGame(game.getGameId(), user2.getUserId(), null);
    
        // Attempt to start the game now that there are two players
        game = gameService.startGame(game.getGameId());
    
        // Check the game's status to confirm it has started
        assertEquals(GameStatus.STARTED, game.getGameStatus());
    }
    

    @Test
    public void testJoinGame() {
        User user1 = new User();
        user1.setUsername("joinerUser1");
        user1.setPassword("joinerPass1");
        userService.createUser(user1);
    
        Game game = gameService.createGame(user1.getUserId());
    
        User user2 = new User();
        user2.setUsername("joinerUser2");
        user2.setPassword("joinerPass2");
        userService.createUser(user2);
    
        // User2 joins the game created by User1
        game = gameService.joinGame(game.getGameId(), user2.getUserId(), null);
    
        assertTrue(game.getPlayers().stream().anyMatch(player -> player.getUser().getUserId().equals(user2.getUserId())));
    }
    
    @Test
    public void testUpdatePlayerScore() {
        User user = new User();
        user.setUsername("scorerUser");
        user.setPassword("scorerPass");
        userService.createUser(user);
    
        Game game = gameService.createGame(user.getUserId());
    
        // Update score
        gameService.updatePlayerScore(game.getGameId(), user.getUserId(), 50);
    
        // Retrieve player to check updated score
        GamePlayer player = gamePlayerRepository.findByGame_GameIdAndUser_UserId(game.getGameId(), user.getUserId()).orElseThrow();
        assertEquals(50, player.getScore());
    }
    

}


