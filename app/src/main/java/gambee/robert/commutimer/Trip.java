package gambee.robert.commutimer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Trip implements Parcelable {
    private ArrayList<TripLeg> legList = new ArrayList<TripLeg>(3);

    public Trip() {}

    public Trip(ArrayList<TripLeg> legs) {
        legList = legs;
    }

    public Trip(JSONObject json) throws JSONException {
        JSONArray legs = json.getJSONArray("Legs");
        ArrayList<TripLeg> list = new ArrayList<TripLeg>(legs.length());
        for (int i = 0; i < legs.length(); ++i) {
            list.add(new TripLeg((JSONObject) legs.get(i)));
        }
        legList = list;
    }

    public Trip(Parcel parcel) throws JSONException {
            this(new JSONObject(parcel.readString()));
    }

    public void addLeg(TripLeg leg) {
        legList.add(leg);
    }

    public void addLegAt(int index, TripLeg leg) {
        legList.add(index, leg);
    }

    public TripLeg getLeg(int index) {
        return legList.get(index);
    }

    public int getSize() {
        return legList.size();
    }

    public Iterator<TripLeg> iterLegs() {
        return legList.iterator();
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        JSONArray legs = new JSONArray();
        Iterator<TripLeg> iter = iterLegs();
        while (iter.hasNext()) {
            TripLeg tl = iter.next();
            legs.put(tl.toJson());
        }
        json.put("Legs", legs);
        return json;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        JSONObject json = new JSONObject();
        try {
            json = toJson();
        }
        catch(JSONException ex) {
            Log.e("CommutimerError", ex.toString());
        }
        parcel.writeString(json.toString());
    }

    public static final Parcelable.Creator<Trip> CREATOR
            = new Parcelable.Creator<Trip>() {
        public Trip createFromParcel(Parcel parcel) {
            try {
                return new Trip(parcel);
            }
            catch (JSONException ex) {
                Log.e("CommutimerError", ex.toString());
                return new Trip();
            }
        }

        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };
}
