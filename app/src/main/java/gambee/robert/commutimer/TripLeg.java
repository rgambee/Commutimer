package gambee.robert.commutimer;

import org.json.JSONObject;

import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TripLeg {
    private String legType = "";
    private GregorianCalendar startTime = new GregorianCalendar(TimeZone.getDefault());
    private GregorianCalendar endTime = new GregorianCalendar(TimeZone.getDefault());

    public TripLeg() {}

    public String getLegType() {
        return legType;
    }

    public boolean setLegType(String newLegType) {
        legType = newLegType;
        return true;
    }

    public GregorianCalendar getStartTime() {
        return startTime;
    }

    public boolean setStartTime(GregorianCalendar newStartTime) {
        startTime = newStartTime;
        return true;
    }

    public GregorianCalendar getEndTime() {
        return endTime;
    }

    public boolean setEndTime(GregorianCalendar newEndTime) {
        endTime = newEndTime;
        return true;
    }

    public JSONObject toJson() {
        return new JSONObject();
    }
}
