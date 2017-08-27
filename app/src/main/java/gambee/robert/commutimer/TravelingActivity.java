package gambee.robert.commutimer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class TravelingActivity extends AppCompatActivity {
    private LinearLayout legListLayout;
    private ArrayList<Chronometer> legTimers = new ArrayList<Chronometer>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveling);

        Intent intent = getIntent();
        Trip trip = (Trip) intent.getParcelableExtra("TripParcel");

        legListLayout = (LinearLayout) findViewById(R.id.traveling_leg_list_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int legNumber = 0;
        Iterator<TripLeg> legIter = trip.iterLegs();
        while (legIter.hasNext()) {
            TripLeg t = legIter.next();
            Log.d("CommutimerDebug", t.getLegType());
            LinearLayout legLayout = new LinearLayout(this);
            legLayout.setOrientation(LinearLayout.HORIZONTAL);
            legLayout.setLayoutParams(params);

            TextView legLabel = new TextView(this);
            legLabel.setText(String.format(Locale.US, "Leg %d", legNumber));
            legLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Large);

            TextView legTypeLabel = new TextView(this);
            legTypeLabel.setText(t.getLegType());
            legTypeLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Menu);

            Chronometer chron = new Chronometer(this);
            legTimers.add(chron);

            legLayout.addView(legLabel);
            legLayout.addView(legTypeLabel);
            legLayout.addView(chron);
            legListLayout.addView(legLayout);

            ++legNumber;
        }
    }

    public void beginTraveling(View view) {
        long tripStartTime = System.currentTimeMillis();
        Chronometer timer = (Chronometer) findViewById(R.id.trip_timer);
        timer.start();
    }
}
