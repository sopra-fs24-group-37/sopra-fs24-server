package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Set;
import java.util.UUID;


public class RoundDTO {
    private UUID gameId;
    private double latitude;
    private double longitude;
    private int roundsPlayed;
    private Set<RoundStatsDTO> roundStats;
    /*
    private Map<Long, Double[]> guesses;
    private Map<Long, Integer> pointsScored;
    private Set<GamePlayer> gamePlayers;
     */

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public void setRoundsPlayed(int roundsPlayed) {
        this.roundsPlayed = roundsPlayed;
    }

    public Set<RoundStatsDTO> getRoundStats() {
        return roundStats;
    }

    public void setRoundStats(Set<RoundStatsDTO> roundStats) {
        this.roundStats = roundStats;
    }

    /*
    public Map<Long, Double[]> getGuesses() {
        return guesses;
    }

    public void setGuesses(Map<Long, Double[]> guesses) {
        this.guesses = guesses;
    }

    public Map<Long, Integer> getPointsScored() {
        return pointsScored;
    }

    public void setPointsScored(Map<Long, Integer> pointsScored) {
        this.pointsScored = pointsScored;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }
     */
}

