package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GamePutDTO {
    private String username;

    public String getUsername() { 
        return username; // username of person to add or remove
    }

    public void setUsername(String username) {
        this.username = username; // username of person to add or remove
    }
}
