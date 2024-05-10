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

    /*
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<Long, Double[]> guesses = new HashMap<>();
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<Long, Integer> pointsScored = new HashMap<>();
     */

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

    public void addNewRoundStats(UUID gameId, long gamePlayerId, String username, int pointsInc, int pointsTotal, double latitude, double longitude) {
        RoundStats roundStats = new RoundStats(gameId, gamePlayerId, username, pointsInc, pointsTotal, latitude, longitude);
        this.roundStats.add(roundStats);
    }

    public void updateRoundStats(long gamePlayerId, int pointsInc, int pointsTotal, double latitude, double longitude) {
        // Iterate over the set of RoundStats
        for (RoundStats roundStats : this.roundStats) {
            // Check if the current RoundStats object matches the gamePlayerId
            if (roundStats.getGamePlayerId() == gamePlayerId) {
                roundStats.setPointsInc(pointsInc);
                roundStats.setPointsTotal(pointsTotal);
                roundStats.setGuess(latitude,longitude);
            }
        }
    }

    public void clearRoundStats(long gamePlayerId) {
        // Iterate over the set of RoundStats
        for (RoundStats roundStats : this.roundStats) {
            // Check if the current RoundStats object matches the gamePlayerId
            if (roundStats.getGamePlayerId() == gamePlayerId) {
                roundStats.setPointsInc(0);
                roundStats.setPointsTotal(0);
                roundStats.setGuess(0,0);
            }
        }
    }

    public Set<RoundStats> getRoundStats() {
        return roundStats;
    }

    /*
    public void setGuess(long gamePlayer, double latitude, double longitude) {
        Double[] coordinates = {latitude, longitude};
        this.guesses.put(gamePlayer, coordinates);
    }


    public Map<Long, Double[]> getGuesses() {
        return guesses;
    }

    public void setPointsScored(long gamePlayer, int pointsIncrease, int pointsTotal) {
        this.pointsScored.put(gamePlayer, pointsIncrease, pointsTotal);
    }

    public Map<Long, Integer, Integer> getPointsScored() {
        return pointsScored;
    }

    public Map<Long, String, Double[], Integer, Integer> getRoundStats() {
        return RoundStats;
    }

    public void setRoundStats(long gamePlayerId, String username, int pointsInc, int pointsTotal, double latitude, double longitude) {
        Double[] coordinates = {latitude, longitude};
        this.RoundStats.put(gamePlayerId, username, pointsInc, pointsTotal, coordinates);
    }
    */
}

