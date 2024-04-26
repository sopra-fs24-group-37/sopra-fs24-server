package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.LinkedHashSet;
import java.util.Set;


public class LeaderboardDTO extends GameGetDTO {
    private Set<GamePlayerDTO> winners;

    public Set<GamePlayerDTO> getWinners() {
        return winners;
    }

    public void setWinners(Set<GamePlayerDTO> winners) {
        this.winners = winners;
    }
}
