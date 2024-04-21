package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

public class UserGetDTO {

  private Long userId;
  private String password;
  private String username;
  private UserStatus status;
  private Integer gamesPlayed;
  private Integer gamesWon;
  private Integer totalScores;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
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
