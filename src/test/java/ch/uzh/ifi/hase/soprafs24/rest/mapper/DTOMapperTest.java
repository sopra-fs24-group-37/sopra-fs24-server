package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;

import java.util.UUID;

public class DTOMapperTest {

    @Test
    public void testConvertUserPostDTOtoEntity() {
        UserPostDTO dto = new UserPostDTO();
        dto.setUsername("testUser");
        dto.setPassword("testPassword");

        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(dto);
        assertEquals(dto.getUsername(), user.getUsername());
        assertEquals(dto.getPassword(), user.getPassword());
        assertNull(user.getStatus());
        assertNull(user.getToken());
    }

    @Test
    public void testConvertEntityToUserGetDTO() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setStatus(UserStatus.ONLINE);
        user.setGamesPlayed(10);
        user.setGamesWon(5);
        user.setTotalScores(500);

        UserGetDTO dto = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
        assertEquals(user.getUserId(), dto.getUserId());
        assertEquals(user.getUsername(), dto.getUsername());
        assertEquals(user.getStatus(), dto.getStatus());
        assertEquals(user.getGamesPlayed(), dto.getGamesPlayed());
        assertEquals(user.getGamesWon(), dto.getGamesWon());
        assertEquals(user.getTotalScores(), dto.getTotalScores());
    }

    @Test
    public void testConvertGamePostDTOtoEntity() {
        GamePostDTO dto = new GamePostDTO();
        Long gameMasterId = 1L; // gameMaster is a Long
        dto.setGameMaster(gameMasterId);
        dto.setPlayers(new HashSet<>());
        dto.setGameStatus(GameStatus.WAITING);

        Game game = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(dto);
        assertEquals(dto.getGameMaster(), game.getGameMaster());
        assertEquals(dto.getPlayers(), game.getPlayers());
        assertEquals(dto.getGameStatus(), game.getGameStatus());
    }

    @Test
    public void testConvertEntityToGameGetDTO() {
        UUID gameId = UUID.randomUUID(); // gameId is a UUID
        Game game = new Game();
        game.setGameId(gameId);
        game.setGameMaster(1L);
        game.setPlayers(new HashSet<>());
        game.setGameStatus(GameStatus.WAITING);

        GameGetDTO dto = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
        assertEquals(game.getGameId(), dto.getGameId());
        assertEquals(game.getGameMaster(), dto.getGameMaster());
        assertEquals(game.getPlayers(), dto.getPlayers());
        assertEquals(game.getGameStatus(), dto.getGameStatus());
    }

    @Test
    public void testConvertEntityToGamePlayerDTO() {
        GamePlayer player = new GamePlayer();
        player.setPlayerId(1L);
        player.setScore(100);
        User user = new User();
        user.setUserId(2L);
        player.setUser(user);

        GamePlayerDTO dto = DTOMapper.INSTANCE.convertEntityToGamePlayerDTO(player);
        assertEquals(player.getPlayerId(), dto.getPlayerId());
        assertEquals(player.getScore(), dto.getScore());
        assertNotNull(dto.getUser());
    }

    @Test
    public void testConvertGamePlayerDTOtoEntity() {
        GamePlayerDTO dto = new GamePlayerDTO();
        dto.setPlayerId(1L);
        dto.setScore(100);

        GamePlayer player = DTOMapper.INSTANCE.convertGamePlayerDTOtoEntity(dto);
        assertEquals(dto.getPlayerId(), player.getPlayerId());
        assertEquals(dto.getScore(), player.getScore());
    }

    @Test
    public void testConvertEntityToLeaderboardDTO() {
        UUID gameId = UUID.randomUUID(); // gameId is a UUID
        Game game = new Game();
        game.setGameId(gameId);
        game.setGameMaster(1L);
        game.setPlayers(new HashSet<>());
        game.setGameStatus(GameStatus.WAITING);

        LeaderboardDTO dto = DTOMapper.INSTANCE.convertEntityToLeaderboardDTO(game);
        assertEquals(game.getGameId(), dto.getGameId());
        assertEquals(game.getGameMaster(), dto.getGameMaster());
        assertEquals(game.getPlayers(), dto.getPlayers());
        assertEquals(game.getGameStatus(), dto.getGameStatus());
        assertNull(dto.getWinners());
    }
}
