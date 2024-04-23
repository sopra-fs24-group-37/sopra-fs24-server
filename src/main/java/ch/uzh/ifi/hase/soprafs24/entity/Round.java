package ch.uzh.ifi.hase.soprafs24.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

//give it
//{"roundId":"1b5bb058-b7ca-4d3a-88b1-2e05501a03d9","roundNr": 1,"gameId":1b5bb058-b7ca-4d3a-88b1-2e05501a03d9,"guesses":[],"URL":"unsplash.org", "Endtime":"02:48:03"}


//i get back
////{"roundId":"1b5bb058-b7ca-4d3a-88b1-2e05501a03d9","roundNr": 1,"gameId":1b5bb058-b7ca-4d3a-88b1-2e05501a03d9,"guesses":[{"playerId":1,"lat":4.888, "long":94.687 }],"URL":"ignore", "SendTime":"02:48:13"}


@Entity
@Table(name = "ROUND")
public class Round implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long roundId;

    public Long getRoundId() {
        return roundId;
    }

    public void setRoundId(Long id) {
        this.roundId = roundId;
    }

}
