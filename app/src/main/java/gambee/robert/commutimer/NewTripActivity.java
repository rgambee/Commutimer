package gambee.robert.commutimer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class NewTripActivity extends AppCompatActivity {
    private int legNumber = 0;
    private LinearLayout legListLayout;
    private ArrayList<TripLeg> tripLegs = new ArrayList<TripLeg>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        Spinner presetSpinner = (Spinner) findViewById(R.id.trip_preset_spinner);
        ArrayAdapter<CharSequence> presetAdapter = ArrayAdapter.createFromResource(this,
                R.array.dummy_trip_presets, android.R.layout.simple_spinner_item);
        presetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        presetSpinner.setAdapter(presetAdapter);

        legListLayout = (LinearLayout) findViewById(R.id.leg_list_layout);
        addNewLeg(new View(this));
    }

    public void addNewLeg(View view) {
        TextView legLabel = new TextView(this);
        legLabel.setText(getString(R.string.leg_label, legNumber));
        legLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Large);

        LinearLayout modeLayout = new LinearLayout(this);
        modeLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin),
                          0, 0,
                          getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin));
        modeLayout.setLayoutParams(params);

        TextView modeLabel = new TextView(this);
        modeLabel.setText(R.string.mode_label);
        modeLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Menu);

        final Spinner modeSpinner = new Spinner(this);
        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(this,
                R.array.leg_types, android.R.layout.simple_spinner_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private int legIndex = legNumber;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tripLegs.get(legIndex).setMode((String) modeSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        modeLayout.addView(modeLabel);
        modeLayout.addView(modeSpinner);

        legListLayout.addView(legLabel);
        legListLayout.addView(modeLayout);
        tripLegs.add(new TripLeg());
        legNumber++;
    }

    public void startNewTrip(View view) {
        Trip trip = new Trip(tripLegs);
        Intent intent = new Intent(this, TravelingActivity.class);
        intent.putExtra("TripParcel", trip);
        startActivity(intent);
    }
}
