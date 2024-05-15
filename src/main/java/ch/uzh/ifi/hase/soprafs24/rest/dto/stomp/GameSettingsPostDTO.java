package ch.uzh.ifi.hase.soprafs24.rest.dto.stomp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameSettingsPostDTO {
    @JsonProperty("latitude")
    private Integer numRounds;
    @JsonProperty("longitude")
    private Integer guessTime;
    @JsonProperty("userId")
    private Boolean setGamePassword;

    public GameSettingsPostDTO() {}

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
