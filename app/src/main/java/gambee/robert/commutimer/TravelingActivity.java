package gambee.robert.commutimer;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class TravelingActivity extends AppCompatActivity {
    private Trip trip = new Trip();
    private ArrayList<Chronometer> legTimers = new ArrayList<Chronometer>(3);
    private ArrayList<LinearLayout> legLayouts = new ArrayList<>(3);
    private int currentLeg = -1;
    private boolean currentLegIsActive = false;

    private static final String TRIP_KEY = "Trip";
    private static final String CURRENT_LEG_KEY = "CurrentLeg";
    private static final String LEG_IS_ACTIVE_KEY = "LegIsActive";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveling);

        if (savedInstanceState != null) {
            trip = savedInstanceState.getParcelable(TRIP_KEY);
            createNewTravelingLayout(trip);
            currentLeg = savedInstanceState.getInt(CURRENT_LEG_KEY);
            currentLegIsActive = savedInstanceState.getBoolean(LEG_IS_ACTIVE_KEY);
        } else {
            Intent intent = getIntent();
            trip = intent.getParcelableExtra("TripParcel");
            createNewTravelingLayout(trip);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(TRIP_KEY, trip);
        outState.putInt(CURRENT_LEG_KEY, currentLeg);
        outState.putBoolean(LEG_IS_ACTIVE_KEY, currentLegIsActive);
        super.onSaveInstanceState(outState);
    }

    public void createNewTravelingLayout(Trip trip) {
        legTimers.clear();
        legLayouts.clear();
        LinearLayout legListLayout = (LinearLayout) findViewById(R.id.traveling_leg_list_layout);
        int legNumber = 0;
        Iterator<TripLeg> legIter = trip.iterLegs();
        while (legIter.hasNext()) {
            TripLeg t = legIter.next();

            LinearLayout legLayout = (LinearLayout) View.inflate(this, R.layout.traveling_timer,
                                                                 legListLayout);
            TextView legLabel = (TextView) findViewById(R.id.traveling_leg_number);
            TextView modeLabel = (TextView) findViewById(R.id.traveling_leg_mode);
            Chronometer timer = (Chronometer) findViewById(R.id.traveling_chronometer);

            // Generate new IDs to avoid conflicts
            legLayout.setId(View.generateViewId());
            legLabel.setId(View.generateViewId());
            modeLabel.setId(View.generateViewId());
            timer.setId(View.generateViewId());

            legLabel.setText(getString(R.string.leg_label, legNumber));
            modeLabel.setText(t.getMode());

            legTimers.add(timer);
            legLayouts.add(legLayout);
            ++legNumber;
        }
    }

    public void updateTraveling(View view) {
        Button b = (Button) findViewById(R.id.traveling_button);
        if (currentLeg < 0) {
            Chronometer timer = (Chronometer) findViewById(R.id.trip_timer);
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();
            ++currentLeg;
        }

        if (currentLeg < trip.getSize()) {
            if (!currentLegIsActive) {
                trip.getLeg(currentLeg).setStartTime(new Date());
                Chronometer timer = legTimers.get(currentLeg);
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
                legLayouts.get(currentLeg).setBackgroundColor(getResources().getColor(
                        R.color.colorActiveLeg, null));
                currentLegIsActive = true;
                b.setText(getString(R.string.button_end_leg));
            } else {
                trip.getLeg(currentLeg).setEndTime(new Date());
                legTimers.get(currentLeg).stop();
                legLayouts.get(currentLeg).setBackgroundColor(getResources().getColor(
                        R.color.colorInactiveLeg, null));
                ++currentLeg;
                currentLegIsActive = false;
                if (currentLeg < trip.getSize()) {
                    b.setText(getString(R.string.button_start_leg));
                } else {
                    Chronometer timer = (Chronometer) findViewById(R.id.trip_timer);
                    timer.stop();
                    b.setText(getString(R.string.button_continue));
                    b.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Intent intent = new Intent(TravelingActivity.this,
                                                       EditTripActivity.class);
                            intent.putExtra("TripParcel", trip);
                            startActivity(intent);
                        }
                    });
                }
            }
        }
    }
}
