package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID gameId;

  @Column(nullable = false)
  private String gameMaster;  // Username of Game Creator

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "game_players", joinColumns = @JoinColumn(name = "game_id"))
  @Column(name = "player")
  private List<String> players;

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

  public List<String> getPlayers() {
    return players;
  }

  public void setPlayers(List<String> players) {
    this.players = players;
  }

  public GameStatus getGameStatus() {
    return gameStatus;
  }

  public void setGameStatus(GameStatus gameStatus) {
    this.gameStatus = gameStatus;
  }

}
