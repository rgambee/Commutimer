package gambee.robert.commutimer;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveling);

        Intent intent = getIntent();
        trip = intent.getParcelableExtra("TripParcel");
        createNewTravelingLayout();
    }

    public void createNewTravelingLayout() {
        legTimers.clear();
        legLayouts.clear();
        LinearLayout legListLayout = (LinearLayout) findViewById(R.id.traveling_leg_list_layout);
        LinearLayout.LayoutParams legLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams legLabelParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        legLabelParams.setMargins(
                getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin),
                0, 0,
                getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin));
        LinearLayout.LayoutParams timerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        timerParams.setMargins(
                0, 0,
                getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin), 0);
        int legNumber = 0;
        Iterator<TripLeg> legIter = trip.iterLegs();
        while (legIter.hasNext()) {
            TripLeg t = legIter.next();
            LinearLayout legLayout = new LinearLayout(this);
            legLayout.setOrientation(LinearLayout.HORIZONTAL);
            legLayout.setLayoutParams(legLayoutParams);

            TextView legLabel = new TextView(this);
            legLabel.setLayoutParams(legLabelParams);
            legLabel.setText(getString(R.string.leg_label, legNumber));
            legLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Large);

            TextView modeLabel = new TextView(this);
            modeLabel.setLayoutParams(legLabelParams);
            modeLabel.setText(t.getMode());
            modeLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Menu);

            Chronometer timer = new Chronometer(this);
            timer.setLayoutParams(timerParams);
            legTimers.add(timer);
            timer.setGravity(Gravity.END);

            legLayout.addView(legLabel);
            legLayout.addView(modeLabel);
            legLayout.addView(timer);
            legLayouts.add(legLayout);
            legListLayout.addView(legLayout);

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
