package gambee.robert.commutimer;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss",
                                                           Locale.US);
        String fileName = (getString(R.string.trip_filename_prefix)
                           + dateFormat.format(trip.getLeg(0).getStartTime())
                           + getString(R.string.trip_filename_extension));
        File file = new File(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), getString(R.string.trip_save_directory)),
                fileName);
        file.getParentFile().mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(trip.toJson().toString(4).getBytes("UTF-8"));
            fos.close();
        }
        catch (JSONException | IOException ex) {
            Log.e("CommutimerError", ex.toString());
        }
    }
}
