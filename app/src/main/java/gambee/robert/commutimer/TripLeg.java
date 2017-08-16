package gambee.robert.commutimer;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TripLeg {
    private String legType = "";
    private Date startTime = new Date(0);
    private Date endTime = new Date(0);

    public TripLeg() {}

    public TripLeg(String legType) {
        this.legType = legType;
    }

    public TripLeg(JSONObject json) throws JSONException {
        String lt = json.getString("LegType");
        String startTimeString = json.getString("StartTime");
        String endTimeString = json.getString("EndTime");

        legType = lt;
        startTime = SimpleDateFormat.getDateTimeInstance().parse(startTimeString,
                                                                 new ParsePosition(0));
        endTime = SimpleDateFormat.getDateTimeInstance().parse(endTimeString,
                                                               new ParsePosition(0));
    }

    public String getLegType() {
        return legType;
    }

    public boolean setLegType(String newLegType) {
        legType = newLegType;
        return true;
    }

    public Date getStartTime() {
        return startTime;
    }

    public boolean setStartTime(Date newStartTime) {
        startTime = newStartTime;
        return true;
    }

    public Date getEndTime() {
        return endTime;
    }

    public boolean setEndTime(Date newEndTime) {
        endTime = newEndTime;
        return true;
    }

    public JSONObject toJson() throws org.json.JSONException {
        JSONObject json = new JSONObject();
        json.put("LegType", legType);
        json.put("StartTime", SimpleDateFormat.getDateTimeInstance().format(startTime));
        json.put("EndTime", SimpleDateFormat.getDateTimeInstance().format(endTime));
        return json;
    }
}
