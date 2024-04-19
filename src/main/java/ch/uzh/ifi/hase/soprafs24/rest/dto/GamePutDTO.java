package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GamePutDTO {
    private Long userId;

    public Long getUserId() { 
        return userId; // userId of person to add or remove
    }

    public void setUserId(Long userId) {
        this.userId = userId; // userId of person to add or remove
    }
}
