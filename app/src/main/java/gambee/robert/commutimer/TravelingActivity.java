package gambee.robert.commutimer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Locale;

public class TravelingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveling);

        // TODO: link this to Trip from NewTripActivity
        Trip dummyTrip = new Trip();
        dummyTrip.addLeg(new TripLeg("Foot"));
        dummyTrip.addLeg(new TripLeg("Bus"));
        dummyTrip.addLeg(new TripLeg("Subway"));
        dummyTrip.addLeg(new TripLeg("Other"));

        LinearLayout legListLayout = (LinearLayout) findViewById(R.id.traveling_leg_list_layout);
        legListLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        legListLayout.setLayoutParams(params);
        int legNumber = 0;
        Iterator<TripLeg> legIter = dummyTrip.iterLegs();
        while (legIter.hasNext()) {
            LinearLayout legLayout = new LinearLayout(this);
            legLayout.setOrientation(LinearLayout.HORIZONTAL);
            legLayout.setLayoutParams(params);

            TextView legLabel = new TextView(this);
            legLabel.setText(String.format(Locale.US, "Leg %d", legNumber));
            legLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Large);
            ++legNumber;

            TextView legTypeLabel = new TextView(this);
            legTypeLabel.setText(legIter.next().getLegType());
            legTypeLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Menu);

            legLayout.addView(legLabel);
            legLayout.addView(legTypeLabel);
            legListLayout.addView(legLayout);
        }
    }

    public void beginTraveling(View view) {
        long tripStartTime = System.currentTimeMillis();
        Chronometer timer = (Chronometer) findViewById(R.id.trip_timer);
        timer.start();
    }
}
