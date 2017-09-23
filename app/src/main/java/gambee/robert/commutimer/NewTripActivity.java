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

import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class NewTripActivity extends AppCompatActivity {
    private int legNumber = 0;
    private LinearLayout legListLayout;
    private ArrayList<TripLeg> tripLegs = new ArrayList<TripLeg>(3);
    private GtfsInfo routeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        routeInfo =  new GtfsInfo(Volley.newRequestQueue(this));

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
        LinearLayout modeLayout = new LinearLayout(this);
        LinearLayout routeLayout = new LinearLayout(this);
        LinearLayout stopsLayout = new LinearLayout(this);

        TextView legLabel = new TextView(this);
        TextView modeLabel = new TextView(this);
        TextView routeLabel = new TextView(this);
        TextView directionLabel = new TextView(this);
        TextView sourceLabel = new TextView(this);
        TextView destinationLabel = new TextView(this);

        final Spinner modeSpinner = new Spinner(this);
        final Spinner routeSpinner = new Spinner(this);
        final Spinner directionSpinner = new Spinner(this);
        final Spinner sourceSpinner = new Spinner(this);
        final Spinner destinationSpinner = new Spinner(this);

        modeLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        modeLayout.setLayoutParams(params);

        legLabel.setText(getString(R.string.leg_label, legNumber));
        legLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Large);

        modeLabel.setText(R.string.mode_label);
        modeLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Menu);
        modeLabel.setPadding(
                getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin),
                0, 0, 0);

        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(this,
                R.array.leg_types, android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private final int legIndex = legNumber;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String mode = (String) modeSpinner.getSelectedItem();
                tripLegs.get(legIndex).setMode(mode);
                updateRoutes(routeSpinner, mode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        modeLayout.addView(legLabel);
        modeLayout.addView(modeLabel);
        modeLayout.addView(modeSpinner);

        routeLabel.setText(R.string.route_label);
        routeLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Menu);
        directionLabel.setText(R.string.direction_label);
        directionLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Menu);

        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private final int legIndex = legNumber;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String route = (String) routeSpinner.getSelectedItem();
                tripLegs.get(legIndex).setRoute(route);
                updateDirections(directionSpinner, route);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        directionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private final int legIndex = legNumber;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String direction = (String) directionSpinner.getSelectedItem();
                tripLegs.get(legIndex).setRouteDirection(direction);
                updateStops(sourceSpinner, tripLegs.get(legIndex).getRoute(), direction);
                updateStops(destinationSpinner, tripLegs.get(legIndex).getRoute(), direction);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        routeLayout.setOrientation(LinearLayout.HORIZONTAL);
        routeLayout.setLayoutParams(params);
        routeLayout.addView(routeLabel);
        routeLayout.addView(routeSpinner);
        routeLayout.addView(directionLabel);
        routeLayout.addView(directionSpinner);

        sourceLabel.setText(R.string.source_label);
        sourceLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Menu);
        destinationLabel.setText(R.string.destination_label);
        destinationLabel.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Menu);

        sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private final int legIndex = legNumber;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String source = (String) sourceSpinner.getSelectedItem();
                tripLegs.get(legIndex).setSource(source);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        destinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private final int legIndex = legNumber;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String destination = (String) destinationSpinner.getSelectedItem();
                tripLegs.get(legIndex).setDestination(destination);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        stopsLayout.setOrientation(LinearLayout.HORIZONTAL);
        stopsLayout.setLayoutParams(params);
        stopsLayout.addView(sourceLabel);
        stopsLayout.addView(sourceSpinner);
        stopsLayout.addView(destinationLabel);
        stopsLayout.addView(destinationSpinner);

        legListLayout.addView(modeLayout);
        legListLayout.addView(routeLayout);
        legListLayout.addView(stopsLayout);
        tripLegs.add(new TripLeg());
        legNumber++;
    }

    private void updateRoutes(Spinner spinner, String mode) {
        ArrayList<String> routes = routeInfo.getRoutesForMode(mode);
        if (routes == null) {
            routes = new ArrayList<>(0);
            spinner.setEnabled(false);
        } else {
            spinner.setEnabled(true);
        }
        ArrayAdapter<String> routeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, routes);
        spinner.setAdapter(routeAdapter);
    }

    private void updateDirections(Spinner spinner, String route) {
        ArrayList<String> directions = routeInfo.getDirectionsForRoute(route);
        if (directions == null) {
            directions = new ArrayList<>(0);
            spinner.setEnabled(false);
        } else {
            spinner.setEnabled(true);
        }
        ArrayAdapter<String> routeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, directions);
        spinner.setAdapter(routeAdapter);
    }

    private void updateStops(Spinner spinner, String route, String direction) {
        ArrayList<String> stops = routeInfo.getStopsForRouteDirection(route, direction);
        if (stops == null) {
            stops = new ArrayList<>(0);
            spinner.setEnabled(false);
        } else {
            spinner.setEnabled(true);
        }
        ArrayAdapter<String> routeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, stops);
        spinner.setAdapter(routeAdapter);
    }

    public void startNewTrip(View view) {
        Trip trip = new Trip(tripLegs);
        Intent intent = new Intent(this, TravelingActivity.class);
        intent.putExtra("TripParcel", trip);
        startActivity(intent);
    }
}
