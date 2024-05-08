package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardDTO;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePlayerDTO;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  /* USER */
  @Mapping(source = "password", target = "password")
  @Mapping(source = "username", target = "username")
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "token", ignore = true)
  @Mapping(target = "gamesPlayed", ignore = true)
  @Mapping(target = "gamesWon", ignore = true)
  @Mapping(target = "totalScores", ignore = true)
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "gamesPlayed", target = "gamesPlayed")
  @Mapping(source = "gamesWon", target = "gamesWon")
  @Mapping(source = "totalScores", target = "totalScores")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "username", target = "username")
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "token", ignore = true)
  @Mapping(target = "gamesPlayed", ignore = true)
  @Mapping(target = "gamesWon", ignore = true)
  @Mapping(target = "totalScores", ignore = true)
  User convertEntityToUserDTO(UserDTO userDTO);

  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "username", target = "username")
  UserDTO convertUserDTOtoEntity(User user);

  /* GAME */
  @Mapping(target = "gameId", ignore = true)
  @Mapping(source = "gameMaster", target = "gameMaster")
  @Mapping(source = "players", target = "players")
  @Mapping(source = "gameStatus", target = "gameStatus")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "guessTime", target = "guessTime")
  Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

  @Mapping(source = "gameId", target = "gameId")
  @Mapping(source = "gameMaster", target = "gameMaster")
  @Mapping(source = "players", target = "players")
  @Mapping(source = "gameStatus", target = "gameStatus")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "guessTime", target = "guessTime")
  GameGetDTO convertEntityToGameGetDTO(Game game);

  /* GAME PLAYER */
  @Mapping(source = "playerId", target = "playerId")
  @Mapping(source = "score", target = "score")
  @Mapping(source = "user", target = "user")
  GamePlayerDTO convertEntityToGamePlayerDTO(GamePlayer gamePlayer);

  @Mapping(source = "playerId", target = "playerId")
  @Mapping(source = "score", target = "score")
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "game", ignore = true)
  GamePlayer convertGamePlayerDTOtoEntity(GamePlayerDTO gamePlayerDTO);

  /* LEADERBOARD */
  @Mapping(source = "gameId", target = "gameId")
  @Mapping(source = "gameMaster", target = "gameMaster")
  @Mapping(source = "players", target = "players")
  @Mapping(source = "gameStatus", target = "gameStatus")
  @Mapping(target = "winners", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "guessTime", ignore = true)
  LeaderboardDTO convertEntityToLeaderboardDTO(Game game);

}
