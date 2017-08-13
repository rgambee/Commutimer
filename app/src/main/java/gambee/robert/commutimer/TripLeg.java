package gambee.robert.commutimer;

import org.json.JSONObject;

import java.time.ZonedDateTime;

public class TripLeg {
    private String legType = "";
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;

    public TripLeg() {}

    public TripLeg(String legType) {
        this.legType = legType;
    }

    public String getLegType() {
        return legType;
    }

    public boolean setLegType(String newLegType) {
        legType = newLegType;
        return true;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public boolean setStartTime(ZonedDateTime newStartTime) {
        startTime = newStartTime;
        return true;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public boolean setEndTime(ZonedDateTime newEndTime) {
        endTime = newEndTime;
        return true;
    }

    public JSONObject toJson() throws org.json.JSONException {
        JSONObject json = new JSONObject();
        json.put("LegType", legType);
        json.put("StartTime", startTime.toString());
        json.put("EndTime", endTime.toString());
        return json;
    }
}
