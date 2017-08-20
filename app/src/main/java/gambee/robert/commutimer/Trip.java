package gambee.robert.commutimer;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Trip implements Parcelable {
    private ArrayList<TripLeg> legList = new ArrayList<TripLeg>(3);
    private static Trip.Creator CREATOR;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel var1, int var2) {
    }
}
