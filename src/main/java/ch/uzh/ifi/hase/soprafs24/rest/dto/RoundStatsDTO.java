package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class RoundStatsDTO {
    private long gamePlayerId;
    private String username;
    private int pointsInc;
    private int pointsTotal;
    private double[] guess;

    public Long getGamePlayerId() {
        return gamePlayerId;
    }

    public void setGamePlayerId(Long gamePlayerId) {
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

    public void setGuess(double[] guess) {
        this.guess = guess;
    }
}
