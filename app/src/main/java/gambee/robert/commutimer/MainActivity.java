package gambee.robert.commutimer;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "gambee.robert.commutimer.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openNewTrip(View view) {
        Intent intent = new Intent(this, NewTripActivity.class);
        startActivity(intent);
    }
}
