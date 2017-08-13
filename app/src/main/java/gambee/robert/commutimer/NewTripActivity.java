package gambee.robert.commutimer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class NewTripActivity extends AppCompatActivity {
    private int legNumber = 1;
    private LinearLayout legListLayout;
    private ArrayList<Spinner> legSpinnerList = new ArrayList<Spinner>(3);

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
        legLabel.setText(String.format(Locale.US, "Leg %d", legNumber));
        legLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Large);

        LinearLayout legTypeLayout = new LinearLayout(this);
        legTypeLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin),
                          0, 0,
                          getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin));
        legTypeLayout.setLayoutParams(params);

        TextView legTypeLabel = new TextView(this);
        legTypeLabel.setText(R.string.leg_type_text);
        legTypeLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Menu);

        Spinner legTypeSpinner = new Spinner(this);
        ArrayAdapter<CharSequence> legTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.leg_types, android.R.layout.simple_spinner_item);
        legTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        legTypeSpinner.setAdapter(legTypeAdapter);
        legSpinnerList.add(legTypeSpinner);
        legTypeLayout.addView(legTypeLabel);
        legTypeLayout.addView(legTypeSpinner);

        legListLayout.addView(legLabel);
        legListLayout.addView(legTypeLayout);
        legNumber++;
    }

    public void startNewTrip(View view) {
        Intent intent = new Intent(this, TravelingActivity.class);
        startActivity(intent);
    }
}
