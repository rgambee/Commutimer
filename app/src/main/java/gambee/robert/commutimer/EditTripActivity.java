package gambee.robert.commutimer;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class EditTripActivity extends AppCompatActivity {
    private int legNumber = 0;
    private Trip trip = new Trip();
    private GtfsInfo routeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        routeInfo =  new GtfsInfo(Volley.newRequestQueue(this));

        Intent intent = getIntent();
        Trip passedTrip = intent.getParcelableExtra("TripParcel");
        if (passedTrip != null) {
            trip = passedTrip;
            setUpExistingTrip();
        } else {
            setUpNewTrip();
        }
    }

    void setUpNewTrip() {
        addNewLeg(new View(this));
        listPresets();

        Button startButton = (Button) findViewById(R.id.edit_trip_main_button);
        startButton.setText(getString(R.string.button_start_trip));
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewTrip(view);
            }
        });
    }

    void setUpExistingTrip() {
        Button saveButton = (Button) findViewById(R.id.edit_trip_main_button);
        saveButton.setText(getString(R.string.button_save));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTrip(view);
            }
        });
        Iterator<TripLeg> iter = trip.iterLegs();
        while (iter.hasNext()) {
            addLeg(iter.next());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /// Presets
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void listPresets() {
        File presetDirectory = new File(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS),
                getString(R.string.app_name)),
                getString(R.string.preset_directory));
        if (!presetDirectory.exists()) {
            presetDirectory.mkdirs();
        }
        class PresetFilter implements FilenameFilter {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(getString(R.string.trip_filename_extension));
            }
        }
        File[] presets = presetDirectory.listFiles(new PresetFilter());
        ArrayList<String> presetNames = new ArrayList<>(presets.length);
        for (File file : presets) {
            presetNames.add(file.getName());
        }

        final Spinner presetsSpinner = (Spinner) findViewById(R.id.preset_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, presetNames);
        presetsSpinner.setAdapter(adapter);
        presetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadPreset((String) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    public void loadPreset(String presetName) {

    }

    public void savePreset(View view) {
        File file = new File(new File(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS),
                getString(R.string.app_name)),
                getString(R.string.preset_directory)),
                "preset0.json");
        file.getParentFile().mkdirs();
        try {
            // TODO: BufferedFileWriter
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(trip.toJson().toString(4).getBytes("UTF-8"));
            fos.close();
            Snackbar snackbar = Snackbar.make(
                    findViewById(R.id.edit_trip_root_item),
                    getString(R.string.saved_message, file.getName()),
                    Snackbar.LENGTH_LONG);
            snackbar.show();
        } catch (JSONException | IOException ex) {
            Log.e("CommutimerError", ex.toString());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /// Trip Editing
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void addNewLeg(View view) {
        TripLeg newLeg = new TripLeg();
        trip.addLeg(newLeg);
        addLeg(newLeg);
    }

    public void addLeg(final TripLeg tripLeg) {
        LinearLayout legListLayout = (LinearLayout) findViewById(R.id.leg_list_layout);
        LinearLayout tripLegLayout = (LinearLayout) View.inflate(this, R.layout.trip_leg,
                                                                 legListLayout);

        TextView legLabel = (TextView) findViewById(R.id.leg_label_text);
        final Spinner modeSpinner = (Spinner) findViewById(R.id.mode_spinner);
        final Spinner routeSpinner = (Spinner) findViewById(R.id.route_spinner);
        final Spinner directionSpinner = (Spinner) findViewById(R.id.direction_spinner);
        final Spinner sourceSpinner = (Spinner) findViewById(R.id.source_spinner);
        final Spinner destinationSpinner = (Spinner) findViewById(R.id.destination_spinner);
        TimePicker startPicker = (TimePicker) findViewById(R.id.start_time_picker);
        TimePicker endPicker = (TimePicker) findViewById(R.id.end_time_picker);
        // Give Views new IDs to avoid conflicts
        View[] needNewIDs = {legLabel, modeSpinner, routeSpinner, directionSpinner,
                             sourceSpinner, destinationSpinner, startPicker, endPicker};
        for (View v : needNewIDs) {
            v.setId(View.generateViewId());
        }

        legLabel.setText(getString(R.string.leg_label, legNumber));

        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(this,
                R.array.leg_types, R.layout.truncated_spinner_text);
        modeSpinner.setAdapter(modeAdapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String mode = (String) modeSpinner.getSelectedItem();
                tripLeg.setMode(mode);
                updateRoutes(routeSpinner, mode, tripLeg.getRoute());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        int modePositon = modeAdapter.getPosition(tripLeg.getMode());
        if (modePositon >= 0 ) {
            modeSpinner.setSelection(modePositon);
        }

        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String route = (String) routeSpinner.getSelectedItem();
                tripLeg.setRoute(route);
                updateDirections(directionSpinner, route, tripLeg.getRouteDirection());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        directionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String direction = (String) directionSpinner.getSelectedItem();
                tripLeg.setRouteDirection(direction);
                updateStops(sourceSpinner, tripLeg.getRoute(), direction,
                            tripLeg.getSource());
                updateStops(destinationSpinner, tripLeg.getRoute(), direction,
                            tripLeg.getDestination());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String source = (String) sourceSpinner.getSelectedItem();
                tripLeg.setSource(source);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        destinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String destination = (String) destinationSpinner.getSelectedItem();
                tripLeg.setDestination(destination);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        startPicker.setVisibility(tripLeg.getStartTime().equals(new Date(0))
                                  ? View.GONE : View.VISIBLE);
        endPicker.setVisibility(tripLeg.getEndTime().equals(new Date(0))
                                ? View.GONE : View.VISIBLE);

        legNumber++;
    }

    private void updateRoutes(Spinner spinner, String mode, String existingRoute) {
        ArrayList<String> routes = routeInfo.getRoutesForMode(mode);
        if (routes == null) {
            routes = new ArrayList<>(0);
            spinner.setEnabled(false);
        } else {
            spinner.setEnabled(true);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.truncated_spinner_text, routes);
        spinner.setAdapter(adapter);
        int routePosition = adapter.getPosition(existingRoute);
        if (routePosition >= 0) {
            spinner.setSelection(routePosition);
        }
    }

    private void updateDirections(Spinner spinner, String route, String existingDirection) {
        ArrayList<String> directions = routeInfo.getDirectionsForRoute(route);
        if (directions == null) {
            directions = new ArrayList<>(0);
            spinner.setEnabled(false);
        } else {
            spinner.setEnabled(true);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.truncated_spinner_text, directions);
        spinner.setAdapter(adapter);
        int directionPosition = adapter.getPosition(existingDirection);
        if (directionPosition >= 0) {
            spinner.setSelection(directionPosition);
        }
    }

    private void updateStops(Spinner spinner, String route, String direction, String existingStop) {
        ArrayList<String> stops = routeInfo.getStopsForRouteDirection(route, direction);
        if (stops == null) {
            stops = new ArrayList<>(0);
            spinner.setEnabled(false);
        } else {
            spinner.setEnabled(true);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.truncated_spinner_text, stops);
        spinner.setAdapter(adapter);
        int stopPosition = adapter.getPosition(existingStop);
        if (stopPosition >= 0) {
            spinner.setSelection(stopPosition);
        }
    }

    public void startNewTrip(View view) {
        Intent intent = new Intent(this, TravelingActivity.class);
        intent.putExtra("TripParcel", trip);
        startActivity(intent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /// Saving
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void saveTrip(View view) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss",
                Locale.US);
        String fileName = (getString(R.string.trip_filename_prefix)
                + dateFormat.format(trip.getLeg(0).getStartTime())
                + getString(R.string.trip_filename_extension));
        File file = new File(new File (new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS),
                getString(R.string.app_name)),
                getString(R.string.trip_save_directory)),
                fileName);
        if (file.exists()) {
            askForOverwriteConfirmation(file);
        } else {
            writeToFile(file);
        }
    }

    public void askForOverwriteConfirmation(final File file) {
        final int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;
        final int HOR_MARGIN = getResources().getDimensionPixelOffset(
                R.dimen.activity_horizontal_margin);
        final int VERT_MARGIN = getResources().getDimensionPixelOffset(
                R.dimen.activity_vertical_margin);

        LinearLayout popupLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                WRAP_CONTENT, WRAP_CONTENT);
        popupLayout.setPadding(HOR_MARGIN, VERT_MARGIN, HOR_MARGIN, VERT_MARGIN);
        popupLayout.setLayoutParams(params);
        popupLayout.setOrientation(LinearLayout.VERTICAL);
        popupLayout.setBackgroundColor(0xffeff0f1);

        TextView popupMessage = new TextView(this);
        popupMessage.setText(getString(R.string.popup_message));
        popupLayout.addView(popupMessage);

        final EditText newFilenameEditText = new EditText(this);
        newFilenameEditText.setText(file.getName(), TextView.BufferType.NORMAL);
        newFilenameEditText.setEnabled(false);
        popupLayout.addView(newFilenameEditText);

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setLayoutParams(params);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        final Button overwriteButton = new Button(this);
        overwriteButton.setText(getString(R.string.button_overwrite));
        final Button renameButton = new Button(this);
        renameButton.setText(getString(R.string.button_rename));
        final Button cancelButton = new Button(this);
        cancelButton.setText(getString(R.string.button_cancel));

        buttonLayout.addView(overwriteButton);
        buttonLayout.addView(renameButton);
        buttonLayout.addView(cancelButton);
        popupLayout.addView(buttonLayout);

        final PopupWindow popup = new PopupWindow(popupLayout, WRAP_CONTENT, WRAP_CONTENT, true);
        CoordinatorLayout editTripLayout = (CoordinatorLayout) findViewById(
                R.id.edit_trip_root_item);
        popup.showAtLocation(editTripLayout, Gravity.CENTER, 0, 0);

        overwriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeToFile(file);
                popup.dismiss();
            }
        });

        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overwriteButton.setEnabled(false);
                newFilenameEditText.setEnabled(true);
                renameButton.setText(getString(R.string.button_save));
                renameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File newFile = new File(file.getParent(),
                                                newFilenameEditText.getText().toString());
                        writeToFile(newFile);
                        popup.dismiss();
                    }
                });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.dismiss();
            }
        });
    }

    public void writeToFile(File file) {
        file.getParentFile().mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(trip.toJson().toString(4).getBytes("UTF-8"));
            fos.close();
            Snackbar snackbar = Snackbar.make(
                    findViewById(R.id.edit_trip_root_item),
                    getString(R.string.saved_message, file.getName()),
                    Snackbar.LENGTH_LONG);

            snackbar.show();
        } catch (JSONException | IOException ex) {
            Log.e("CommutimerError", ex.toString());
        }
    }
}

