package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;


@Entity
@Table(name = "GAME")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "gameId")
public class Game implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID gameId;

  @Column(nullable = false)
  private Long gameMaster;

  @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private Set<GamePlayer> players = new HashSet<>();

  @Column(nullable = false)
  private GameStatus gameStatus;

  @Column(nullable = true) // Nullable since it's optional
  private Integer guessTime;

  @Column(nullable = true) // Nullable since it's optional
  private Integer password;

  @Column(nullable = false)
  private Integer numRounds;

  public UUID getGameId() {
    return gameId;
  }

  public void setGameId(UUID gameId) {
    this.gameId = gameId;
  }

  public Long getGameMaster() {
    return gameMaster;
  }

  public void setGameMaster(Long gameMaster) {
    this.gameMaster = gameMaster;
  }

  public Set<GamePlayer> getPlayers() {
    return players;
  }

  public void setPlayers(Set<GamePlayer> players) {
    this.players = players;
  }

  public void addNewPlayer(User user) {
    GamePlayer gamePlayer = new GamePlayer(this, user, 0);
    this.players.add(gamePlayer);
  }

  public GameStatus getGameStatus() {
    return gameStatus;
  }

  public void setGameStatus(GameStatus gameStatus) {
    this.gameStatus = gameStatus;
  }

  public Integer getGuessTime() {
    return guessTime;
  }

  public void setGuessTime(Integer guessTime) {
    this.guessTime = guessTime;
  }

  public Integer getPassword() {
    return password;
  }

  public void setPassword(Integer password) {
    this.password = password;
  }

  public Integer getNumRounds() {
    return numRounds;
  }

  public void setNumRounds(Integer numRounds) {
    this.numRounds = numRounds;
  }

}
