package ch.uzh.ifi.hase.soprafs24.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "ROUND")
public class Round implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID gameId;

    private int checkIn;

    private double latitude;

    private double longitude;

    private String pictureId;

    private int roundsPlayed;

    @ElementCollection
    @CollectionTable(name = "round_guesses", joinColumns = @JoinColumn(name = "round_id"))
    @MapKeyColumn(name = "player_id")
    @Column(name = "guess_coordinates")
    private Map<Long, Double[]> guesses = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "round_points_scored", joinColumns = @JoinColumn(name = "round_id"))
    @MapKeyColumn(name = "player_id")
    @Column(name = "points_scored")
    private Map<Long, Integer> pointsScored = new HashMap<>();

    public Round() {
        this.guesses = new HashMap<>();
        this.pointsScored = new HashMap<>();
    }

    public String getPictureId() {return pictureId;}

    public void setPictureId(String pictureId) {this.pictureId = pictureId;}

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID id) {
        this.gameId = id;
    }

    public int getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(int i) {
        this.checkIn = i;
    }

    public void incCheckIn(){this.checkIn= this.checkIn+1;}

    public void clearCheckIn(){this.checkIn= 0;}

    public double getLatitude() {return latitude;}

    public void setLatitude(double latitude) {this.latitude = latitude;}

    public double getLongitude() {return longitude;}

    public void setLongitude(double longitude) {this.longitude = longitude;}

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public void setRoundsPlayed(int i) {
        this.roundsPlayed = i;
    }

    public void incRoundsPlayed(){this.roundsPlayed= this.roundsPlayed+1;}

    public void clearRoundsPlayed(){this.roundsPlayed= 0;}

    public void setGuess(long gamePlayer, double latitude, double longitude) {
        Double[] coordinates = {latitude, longitude};
        this.guesses.put(gamePlayer, coordinates);
    }

    public Map<Long, Double[]> getGuesses() {
        return guesses;
    }

    public void setPointsScored(long gamePlayer, int points) {
        this.pointsScored.put(gamePlayer, points);
    }

    public Map<Long, Integer> getPointsScored() {
        return pointsScored;
    }
}

