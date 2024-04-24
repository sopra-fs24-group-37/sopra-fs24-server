package ch.uzh.ifi.hase.soprafs24.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

//give it
//{"roundId":"1b5bb058-b7ca-4d3a-88b1-2e05501a03d9","roundNr": 1,"gameId":1b5bb058-b7ca-4d3a-88b1-2e05501a03d9,"guesses":[],"URL":"unsplash.org", "Endtime":"02:48:03"}


//i get back
////{"roundId":"1b5bb058-b7ca-4d3a-88b1-2e05501a03d9","roundNr": 1,"gameId":1b5bb058-b7ca-4d3a-88b1-2e05501a03d9,"guesses":[{"playerId":1,"lat":4.888, "long":94.687 }],"URL":"ignore", "SendTime":"02:48:13"}


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

}
