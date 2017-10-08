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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EditTripActivity extends AppCompatActivity {
    private boolean existingTrip = false;
    private int legNumber = 0;
    private LinearLayout legListLayout;
    private Trip trip = new Trip();
    private GtfsInfo routeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        routeInfo =  new GtfsInfo(Volley.newRequestQueue(this));
        legListLayout = (LinearLayout) findViewById(R.id.leg_list_layout);

        Intent intent = getIntent();
        Trip passedTrip = intent.getParcelableExtra("TripParcel");
        if (passedTrip != null) {
            trip = passedTrip;
            existingTrip = true;
            setUpExistingTrip();
        } else {
            existingTrip = false;
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
                trip.getLeg(legIndex).setMode(mode);
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
                trip.getLeg(legIndex).setRoute(route);
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
                trip.getLeg(legIndex).setRouteDirection(direction);
                updateStops(sourceSpinner, trip.getLeg(legIndex).getRoute(), direction);
                updateStops(destinationSpinner, trip.getLeg(legIndex).getRoute(), direction);
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
                trip.getLeg(legIndex).setSource(source);
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
                trip.getLeg(legIndex).setDestination(destination);
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
        trip.addLeg(new TripLeg());
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, routes);
        spinner.setAdapter(adapter);
    }

    private void updateDirections(Spinner spinner, String route) {
        ArrayList<String> directions = routeInfo.getDirectionsForRoute(route);
        if (directions == null) {
            directions = new ArrayList<>(0);
            spinner.setEnabled(false);
        } else {
            spinner.setEnabled(true);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, directions);
        spinner.setAdapter(adapter);
    }

    private void updateStops(Spinner spinner, String route, String direction) {
        ArrayList<String> stops = routeInfo.getStopsForRouteDirection(route, direction);
        if (stops == null) {
            stops = new ArrayList<>(0);
            spinner.setEnabled(false);
        } else {
            spinner.setEnabled(true);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, stops);
        spinner.setAdapter(adapter);
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

