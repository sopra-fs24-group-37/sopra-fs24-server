package ch.uzh.ifi.hase.soprafs24.rest.dto.stomp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GuessPostDTO {
    @JsonProperty("latitude")
    private double latitude;
    
    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("userId")
    private long userId;
    
    @JsonProperty("useDoubleScore")
    private Boolean useDoubleScore;

    public GuessPostDTO() {}

    public GuessPostDTO(double latitude, double longitude, long userId, Boolean useDoubleScore) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.useDoubleScore = useDoubleScore;
    }

    public double getLat() {return latitude;}

    public double getLng() {return longitude;}

    public void setLat(double latitude) {this.latitude = latitude;}

    public void setLng(double longitude) {this.longitude = longitude;}

    public Long getUserId() {return userId;}

    public void setUserId(long id) {this.userId = id;}

    public Boolean getUseDoubleScore() {return useDoubleScore;}

    public void setUseDoubleScore(Boolean useDoubleScore) {this.useDoubleScore = useDoubleScore;}

}
