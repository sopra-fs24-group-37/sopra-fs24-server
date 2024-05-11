package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO;


public class GamePlayerDTO {
    private Long playerId;
    private UserDTO user;
    private Integer score;
    private Boolean doubleScore;
    private Boolean cantonHint;
    private Boolean multipleCantonHint;

    public Long getPlayerId() {
        return playerId;
      }
    
    public void setPlayerId(Long playerId) {
      this.playerId = playerId;
    }

    public UserDTO getUser() {
      return user;
      }
    
    public void setUser(UserDTO user) {
      this.user = user;
    }

    public Integer getScore() {
      return score;
    }
  
    public void setScore(Integer score) {
      this.score = score;
    }

    public Boolean getDoubleScore() {
      return doubleScore;
    }

    public void setDoubleScore(Boolean doubleScore) {
      this.doubleScore = doubleScore;
    }

    public Boolean getCantonHint() {
      return cantonHint;
    }

    public void setCantonHint(Boolean cantonHint) {
      this.cantonHint = cantonHint;
    }

    public Boolean getMultipleCantonHint() {
      return multipleCantonHint;
    }

    public void setMultipleCantonHint(Boolean multipleCantonHint) {
      this.multipleCantonHint = multipleCantonHint;
    }
}