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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EditTripActivity extends AppCompatActivity {
    Trip trip = new Trip();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        Intent intent = getIntent();
        trip = intent.getParcelableExtra("TripParcel");
    }

    public void saveTrip(View view) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss",
                Locale.US);
        String fileName = (getString(R.string.trip_filename_prefix)
                + dateFormat.format(trip.getLeg(0).getStartTime())
                + getString(R.string.trip_filename_extension));
        File file = new File(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), getString(R.string.trip_save_directory)),
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

    public void renameClick(View view) {
        return;
    }
}

