package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePutDTO;

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

  @Mapping(source = "password", target = "password")
  @Mapping(source = "username", target = "username")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "token", ignore = true)
  @Mapping(target = "gamesPlayed", ignore = true)
  @Mapping(target = "gamesWon", ignore = true)
  @Mapping(target = "pointsScored", ignore = true)
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "gamesPlayed", target = "gamesPlayed")
  @Mapping(source = "gamesWon", target = "gamesWon")
  @Mapping(source = "pointsScored", target = "pointsScored")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(target = "gameId", ignore = true)
  @Mapping(source = "gameMaster", target = "gameMaster")
  @Mapping(source = "players", target = "players")
  @Mapping(source = "gameStatus", target = "gameStatus")
  Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

  @Mapping(source = "gameId", target = "gameId")
  @Mapping(source = "gameMaster", target = "gameMaster")
  @Mapping(source = "players", target = "players")
  @Mapping(source = "gameStatus", target = "gameStatus")
  GameGetDTO convertEntityToGameGetDTO(Game game);

  @Mapping(target = "username", ignore = true)
  GamePutDTO convertGameGetDTOToGamePutDTO(GameGetDTO gameGetDTO);

}
