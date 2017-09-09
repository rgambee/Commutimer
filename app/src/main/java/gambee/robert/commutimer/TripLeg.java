package gambee.robert.commutimer;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TripLeg {
    private Date startTime = new Date(0);
    private Date endTime = new Date(0);
    private String legType = "";
    private String route = "";
    private String routeDirection = "";
    private String source = "";
    private String destination = "";
    final private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            Locale.US);

    public TripLeg() {}

    public TripLeg(String legType) {
        this.legType = legType;
    }

    public TripLeg(String legType, String route, String routeDirection,
                   String source, String destination) {
        this.legType = legType;
        this.route = route;
        this.routeDirection = routeDirection;
        this.source = source;
        this.destination = destination;
    }

    public TripLeg(JSONObject json) throws JSONException, ParseException {
        String startTimeString = json.getString("StartTime");
        String endTimeString = json.getString("EndTime");
        startTime = stringToDate(startTimeString);
        endTime = stringToDate(endTimeString);
        legType = json.getString("LegType");
        route = json.getString("Route");
        routeDirection = json.getString("RouteDirection");
        source = json.getString("Source");
        destination = json.getString("Destination");
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

    public String getStartTimeString() {
        return dateToString(startTime);
    }

    public boolean setStartTime(Date newStartTime) {
        startTime = newStartTime;
        return true;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getEndTimeString() {
        return dateToString(endTime);
    }

    public boolean setEndTime(Date newEndTime) {
        endTime = newEndTime;
        return true;
    }

    public JSONObject toJson() throws org.json.JSONException {
        JSONObject json = new JSONObject();
        json.put("StartTime", getStartTimeString());
        json.put("EndTime", getEndTimeString());
        json.put("LegType", legType);
        json.put("Route", route);
        json.put("RouteDirection", routeDirection);
        json.put("Source", source);
        json.put("Destination", destination);
        return json;
    }

    private String dateToString(Date d) {
        return DATE_FORMAT.format(d);
    }

    private Date stringToDate(String s) throws ParseException {
        return DATE_FORMAT.parse(s);
    }
}
