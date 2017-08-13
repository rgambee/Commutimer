package gambee.robert.commutimer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Trip {
    private ArrayList<TripLeg> legList = new ArrayList<TripLeg>(3);

    public Trip() {}

    public Trip(ArrayList<TripLeg> legs) {
        legList = legs;
    }

    public void addLeg(TripLeg leg) {
        legList.add(leg);
    }

    public void addLegAt(int index, TripLeg leg) {
        legList.add(index, leg);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
       json.put("Legs", new JSONArray(legList));
        return json;
    }
}
