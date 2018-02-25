package gambee.robert.commutimer;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

public class TravelingActivity extends BackConfirmationActivity {
    private Trip trip = new Trip();
    private ArrayList<LinearLayout> legLayouts = new ArrayList<>(3);
    private ArrayList<Chronometer> legTimers = new ArrayList<>(3);
    private ArrayList<Long> elapsedTimes = new ArrayList<>(3);
    private int currentLeg = 0;
    private boolean currentLegIsActive = false;

    private static final String TRIP_KEY = "Trip";
    private static final String CURRENT_LEG_KEY = "CurrentLeg";
    private static final String LEG_IS_ACTIVE_KEY = "LegIsActive";
    private static final String ELAPSED_TIMES_KEY = "ElapsedTimes";
    private static final int NOTIFICATION_ID = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveling);

        if (savedInstanceState != null) {
            loadInstanceState(savedInstanceState);
        } else {
            Intent intent = getIntent();
            trip = intent.getParcelableExtra("TripParcel");
            createNewTravelingLayout(trip);
            // elapsedTimes has one extra element to hold global timer's state
            elapsedTimes = new ArrayList<>(Collections.nCopies(trip.getSize() + 1, 0L));
        }
        // Create notification for transitioning legs
        setNotification();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(currentLeg > 0 || currentLegIsActive,
                            (CoordinatorLayout) findViewById(R.id.traveling_activity_root_item));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(TRIP_KEY, trip);
        outState.putInt(CURRENT_LEG_KEY, currentLeg);
        outState.putBoolean(LEG_IS_ACTIVE_KEY, currentLegIsActive);
        if (currentLegIsActive) {
            long currentLegElapsed = SystemClock.elapsedRealtime() - legTimers.get(currentLeg).getBase();
            elapsedTimes.set(currentLeg, currentLegElapsed);
        }
        if (currentLeg < trip.getSize()) {
            Chronometer globalTimer = (Chronometer) findViewById(R.id.trip_timer);
            long globalTimerBase = globalTimer.getBase();
            elapsedTimes.set(elapsedTimes.size() - 1,
                             SystemClock.elapsedRealtime() - globalTimerBase);
        }
        outState.putSerializable(ELAPSED_TIMES_KEY, elapsedTimes);
        super.onSaveInstanceState(outState);
    }

    public void loadInstanceState(Bundle instate) {
        trip = instate.getParcelable(TRIP_KEY);
        currentLeg = instate.getInt(CURRENT_LEG_KEY);
        currentLegIsActive = instate.getBoolean(LEG_IS_ACTIVE_KEY);
        elapsedTimes = (ArrayList<Long>) instate.getSerializable(ELAPSED_TIMES_KEY);
        createNewTravelingLayout(trip);
        Chronometer globalTimer = (Chronometer) findViewById(R.id.trip_timer);
        globalTimer.setBase(
                SystemClock.elapsedRealtime() - elapsedTimes.get(elapsedTimes.size() - 1)
        );
        if (currentLeg < trip.getSize() || (currentLeg == 0 && currentLegIsActive)) {
            globalTimer.start();
        }
        for (int i = 0; i < legTimers.size(); ++i) {
            legTimers.get(i).setBase(SystemClock.elapsedRealtime() - elapsedTimes.get(i));
            if (i == currentLeg && currentLegIsActive) {
                legTimers.get(i).start();
            }
        }
        setButtonText();
        setBackgroundColors();
    }

    public void setNotification() {
        Intent tapIntent = new Intent(this, TravelingActivity.class);
        PendingIntent pendingTapIntent = PendingIntent.getActivity(this, 0,
                                                                   tapIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext());
        builder.setSmallIcon(R.drawable.notification_icon).setContentTitle("Commutimer");
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC).setOnlyAlertOnce(true);
        builder.setContentIntent(pendingTapIntent);
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(NOTIFICATION_ID, builder.build());
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

            legLayouts.add(legLayout);
            legTimers.add(timer);
            ++legNumber;
        }
    }

    public void updateTraveling(View view) {
        if (currentLeg < trip.getSize()) {
            if (!currentLegIsActive) {
                currentLegIsActive = true;
            } else {
                ++currentLeg;
                currentLegIsActive = false;
            }
            setTripStartEnd();
            startStopTimers();
            setButtonText();
            setBackgroundColors();
        } else {
            Intent intent = new Intent(TravelingActivity.this,
                                       EditTripActivity.class);
            intent.putExtra("TripParcel", trip);
            startActivity(intent);
        }
    }

    void setTripStartEnd() {
        if (currentLegIsActive) {
            trip.getLeg(currentLeg).setStartTime(new Date());
        } else {
            trip.getLeg(currentLeg - 1).setEndTime(new Date());
        }
    }

    void startStopTimers() {
        Chronometer globalTimer = (Chronometer) findViewById(R.id.trip_timer);
        if (currentLegIsActive) {
            if (currentLeg == 0) {
                globalTimer.setBase(SystemClock.elapsedRealtime());
                globalTimer.start();
            }
            Chronometer currentTimer = legTimers.get(currentLeg);
            currentTimer.setBase(SystemClock.elapsedRealtime());
            currentTimer.start();
        } else {
            if (currentLeg == trip.getSize()) {
                globalTimer.stop();
                elapsedTimes.set(trip.getSize(),
                                 SystemClock.elapsedRealtime() - globalTimer.getBase());
            }
            Chronometer previousTimer = legTimers.get(currentLeg - 1);
            previousTimer.stop();
            elapsedTimes.set(currentLeg - 1,
                             SystemClock.elapsedRealtime() - previousTimer.getBase());
        }
    }

    void setButtonText() {
        Button mainButton = (Button) findViewById(R.id.traveling_button);
        if (currentLeg < trip.getSize()) {
            if (currentLegIsActive) {
                mainButton.setText(getString(R.string.button_end_leg));
            } else {
                mainButton.setText(getString(R.string.button_start_leg));
            }
        } else {
            mainButton.setText(getString(R.string.button_continue));
        }
    }

    void setBackgroundColors() {
        for (int i = 0; i < legLayouts.size(); ++i) {
            int colorID = R.color.colorInactiveLeg;
            if (i == currentLeg && currentLegIsActive) {
                colorID = R.color.colorActiveLeg;
            }
            legLayouts.get(i).setBackgroundColor(getResources().getColor(colorID, null));
        }
    }
}
