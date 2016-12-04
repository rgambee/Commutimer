package gambee.robert.commutimer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;

public class TravelingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveling);
    }

    public void beginTraveling(View view) {
        long tripStartTime = System.currentTimeMillis();
        Chronometer timer = (Chronometer) findViewById(R.id.trip_timer);
        timer.start();
    }
}
