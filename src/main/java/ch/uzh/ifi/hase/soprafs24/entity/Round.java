package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "ROUND")
public class Round implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID gameId;

    private int checkIn;

    private double latitude;

    private double longitude;

    private int roundsPlayed;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<RoundStats> roundStats = new HashSet<>();

    public UUID getGameId() {return gameId;}

    public void setGameId(UUID id) {this.gameId = id;}

    public int getCheckIn() {return checkIn;}

    public void setCheckIn(int i) {this.checkIn = i;}

    public void incCheckIn(){this.checkIn= this.checkIn+1;}

    public void clearCheckIn(){this.checkIn= 0;}

    public double getLatitude() {return latitude;}

    public void setLatitude(double latitude) {this.latitude = latitude;}

    public double getLongitude() {return longitude;}

    public void setLongitude(double longitude) {this.longitude = longitude;}

    public int getRoundsPlayed() {return roundsPlayed;}

    public void setRoundsPlayed(int i) {this.roundsPlayed = i;}

    public void incRoundsPlayed(){this.roundsPlayed= this.roundsPlayed+1;}

    public void clearRoundsPlayed(){this.roundsPlayed= 0;}

    public void addExistingRoundStats(RoundStats roundStats) {this.roundStats.add(roundStats);}

    public Set<RoundStats> getRoundStats() {
        return roundStats;
    }
}

