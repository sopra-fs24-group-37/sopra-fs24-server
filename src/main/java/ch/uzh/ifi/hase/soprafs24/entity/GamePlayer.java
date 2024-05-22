package ch.uzh.ifi.hase.soprafs24.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;


@Entity
@Table(name = "game_players")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "playerId")
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer score; // Score for a specific game, updated per round
    private Boolean doubleScore = true;
    private Boolean cantonHint = true;
    private Boolean multipleCantonHint = true;

    // Constructors
    public GamePlayer() {
    }

    public GamePlayer(Game game, User user, Integer score) {
        this.game = game;
        this.user = user;
        this.score = score;
        
    }

    // Getters and setters
    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getDoubleScore() {
        return doubleScore;
    }

    public void setDoubleScore(Boolean doubleScore) {
        this.doubleScore = doubleScore;
    }

    public Boolean getCantonHint() {
        return cantonHint;
    }

    public void setCantonHint(Boolean cantonHint) {
        this.cantonHint = cantonHint;
    }

    public Boolean getMultipleCantonHint() {
        return multipleCantonHint;
    }

    public void setMultipleCantonHint(Boolean multipleCantonHint) {
        this.multipleCantonHint = multipleCantonHint;
    }
    
}
