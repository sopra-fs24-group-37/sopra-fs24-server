package ch.uzh.ifi.hase.soprafs24.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;

@Entity
@Table(name = "ROUNDSTATS")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "roundStatsId")
public class RoundStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roundStatsId;

    @ManyToOne(fetch = FetchType.EAGER)  // I set this to Many to Many since game_id will repeat
    @JoinColumn(name="game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private GamePlayer gamePlayer;

    private int pointsInc;

    private double[] guess;

    public RoundStats() {
    }
    
    public RoundStats(Game game, GamePlayer gamePlayer, int pointsInc, double latitude, double longitude) {
        this.game = game;
        this.gamePlayer = gamePlayer;
        double[] coordinates = {latitude, longitude};
        this.pointsInc = pointsInc;
        this.guess = coordinates;
    }

    public Long getRoundStatsId() {
        return roundStatsId;
    }

    public void setRoundStatsId(Long roundStatsId) {
        this.roundStatsId = roundStatsId;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }    

    public int getPointsInc() {
        return pointsInc;
    }

    public void setPointsInc(int pointsInc) {
        this.pointsInc = pointsInc;
    }

    public double[] getGuess() {
        return guess;
    }

    public void setGuess(double latitude, double longitude) {
        double[] coordinates = {latitude, longitude};
        this.guess = coordinates;
    }

    public void updateRoundStats(int pointsInc, double latitude, double longitude) {
        this.pointsInc = pointsInc;
        double[] coordinates = {latitude, longitude};
        this.guess = coordinates;
    }

    public void clearRoundStats() {
        this.pointsInc = 0;
        double[] coordinates = {0, 0};
        this.guess = coordinates;
    }
}
