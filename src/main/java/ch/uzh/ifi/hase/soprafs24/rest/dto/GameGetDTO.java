package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePlayerDTO;


import java.util.Set;
import java.util.UUID;

public class GameGetDTO {

    private UUID gameId;
    private Long gameMaster;
    private Set<GamePlayerDTO> players;
    private GameStatus gameStatus;

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

    public Set<GamePlayerDTO> getPlayers() {
        return players;
    }

    public void setPlayers(Set<GamePlayerDTO> players) {
        this.players = players;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }
}
