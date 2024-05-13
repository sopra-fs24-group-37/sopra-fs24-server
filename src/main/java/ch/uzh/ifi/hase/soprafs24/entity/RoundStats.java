package ch.uzh.ifi.hase.soprafs24.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "ROUNDSTATS")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "roundStatsId")
public class RoundStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roundStatsId;

    private UUID gameId;
    private long gamePlayerId;

    private String username;

    private int pointsInc;

    private int pointsTotal;

    private double[] guess;

    public RoundStats() {
    }
    public RoundStats(UUID gameId, long gamePlayerId, String username, int pointsInc, int pointsTotal, double latitude, double longitude) {
        double[] coordinates = {latitude, longitude};
        this.gameId = gameId;
        this.gamePlayerId = gamePlayerId;
        this.username = username;
        this.pointsInc = pointsInc;
        this.pointsTotal = pointsTotal;
        this.guess = coordinates;
    }

    public long getGamePlayerId() {
        return gamePlayerId;
    }

    public void setGamePlayerId(long gamePlayerId) {
        this.gamePlayerId = gamePlayerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPointsInc() {
        return pointsInc;
    }

    public void setPointsInc(int pointsInc) {
        this.pointsInc = pointsInc;
    }

    public int getPointsTotal() {
        return pointsTotal;
    }

    public void setPointsTotal(int pointsTotal) {
        this.pointsTotal = pointsTotal;
    }

    public double[] getGuess() {
        return guess;
    }

    public void setGuess(double latitude, double longitude) {
        double[] coordinates = {latitude, longitude};
        this.guess = coordinates;
    }
}
