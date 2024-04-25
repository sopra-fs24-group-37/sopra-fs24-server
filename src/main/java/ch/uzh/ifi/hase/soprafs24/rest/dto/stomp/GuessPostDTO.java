package ch.uzh.ifi.hase.soprafs24.rest.dto.stomp;

public class GuessPostDTO {
    private double latitude;
    private double longitude;
    private long userId;

    public GuessPostDTO() {}

    public GuessPostDTO(double latitude, double longitude, long userId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
    }

    public double getLat() {return latitude;}

    public double getLng() {return longitude;}

    public void setLat(double latitude) {this.latitude = latitude;}

    public void setLng(double longitude) {this.longitude = longitude;}

    public Long getUserId() {return userId;}

    public void setUserId(long id) {this.userId = id;}

}
