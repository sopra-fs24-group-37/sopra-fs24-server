package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class RoundStatsDTO {
    private GamePlayerDTO gamePlayer;
    private int pointsInc;
    private double[] guess;

    public GamePlayerDTO getGamePlayer() {return gamePlayer;}

    public void setGamePlayer(GamePlayerDTO gamePlayer) {this.gamePlayer = gamePlayer;}

    public int getPointsInc() {return pointsInc;}

    public void setPointsInc(int pointsInc) {
        this.pointsInc = pointsInc;
    }

    public double[] getGuess() {
        return guess;
    }

    public void setGuess(double[] guess) {
        this.guess = guess;
    }
}
