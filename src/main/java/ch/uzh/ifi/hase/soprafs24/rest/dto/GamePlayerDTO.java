package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GamePlayerDTO {
    private Long playerId;
    private Integer score;

    public Long getPlayerId() {
        return playerId;
      }
    
      public void setPlayerId(Long playerId) {
        this.playerId = playerId;
      }

      public Integer getScore() {
        return score;
      }
    
      public void setScore(Integer score) {
        this.score = score;
      }
}