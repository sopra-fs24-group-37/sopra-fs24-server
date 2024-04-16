package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;
import java.util.Arrays;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID gameId;

  @Column(nullable = false)
  private String gameMaster;  // Username of Game Creator

  @Column(nullable = false)
  private String players;  // Comma-separated list of usernames

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GameStatus gameStatus;


  public UUID getGameId() {
    return gameId;
  }

  public void setGameId(UUID gameId) {
    this.gameId = gameId;
  }

  public String getGameMaster() {
    return gameMaster;
  }

  public void setGameMaster(String gameMaster) {
    this.gameMaster = gameMaster;
  }

  public String getPlayers() {
    return players;
  }

  public void setPlayers(String players) {
    this.players = players;
  }

  public GameStatus getGameStatus() {
    return gameStatus;
  }

  public void setGameStatus(GameStatus gameStatus) {
    this.gameStatus = gameStatus;
  }

  public void addPlayer(String username) {
    if (players == null || players.isEmpty()) {
      players = username;
    } else {
      players += "," + username;
    }
  }

  public void removePlayer(String username) {
    if (players != null && !players.isEmpty()) {
      String[] playerArray = players.split(",");
      players = String.join(",", Arrays.stream(playerArray)
                                       .filter(name -> !name.equals(username))
                                       .toArray(String[]::new));
    }
  }
}
