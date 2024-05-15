package ch.uzh.ifi.hase.soprafs24.rest.dto.stomp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameSettingsPostDTO {
    @JsonProperty("numRounds")
    private Integer numRounds;
    @JsonProperty("guessTime")
    private Integer guessTime;
    @JsonProperty("setGamePassword")
    private Boolean setGamePassword;

    public GameSettingsPostDTO() {
    }

    public GameSettingsPostDTO(Integer numRounds, Integer guessTime, Boolean setGamePassword) {
        this.numRounds = numRounds;
        this.guessTime = guessTime;
        this.setGamePassword = setGamePassword;
    }

    public Integer getNumRounds() {
        return numRounds;
    }

    public void setNumRounds(Integer numRounds) {
        this.numRounds = numRounds;
    }

    public Integer getGuessTime() {
        return guessTime;
    }

    public void setGuessTime(Integer guessTime) {
        this.guessTime = guessTime;
    }

    public Boolean getSetGamePassword() {
        return setGamePassword;
    }

    public void setSetGamePassword(Boolean setGamePassword) {
        this.setGamePassword = setGamePassword;
    }

}
