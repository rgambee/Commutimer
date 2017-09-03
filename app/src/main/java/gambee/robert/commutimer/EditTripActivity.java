package gambee.robert.commutimer;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class EditTripActivity extends AppCompatActivity {
    Trip trip = new Trip();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        Intent intent = getIntent();
        trip = intent.getParcelableExtra("TripParcel");
    }

    public void saveTrip(View view) {
        String fileName = "Trip_" + trip.getLeg(0).getStartTime().toString() + ".txt";
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), fileName);
        file.getParentFile().mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(trip.toJson().toString(4).getBytes());
            fos.close();
        }
        catch (JSONException | IOException ex) {
            Log.e("CommutimerError", ex.toString());
        }
    }
}
