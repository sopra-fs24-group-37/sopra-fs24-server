package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long userId;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private UserStatus status;

  @Column(nullable = false)
  private Integer gamesPlayed;

  @Column(nullable = false)
  private Integer gamesWon;

  @Column(nullable = false)
  private Integer totalScores;


  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long id) {
    this.userId = userId;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public void setGamesPlayed(Integer gamesPlayed) {
    this.gamesPlayed = gamesPlayed;
  }

  public Integer getGamesPlayed() {
    return gamesPlayed;
  }

  public void setGamesWon(Integer gamesWon) {
    this.gamesWon = gamesWon;
  }

  public Integer getGamesWon() {
    return gamesWon;
  }

  public void setTotalScores(Integer totalScores) {
    this.totalScores = totalScores;
  }

  public Integer getTotalScores() {
    return totalScores;
  }
}
