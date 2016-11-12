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

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

        File outputFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "testFile.txt");
        outputFile.mkdirs();
        try {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(message.getBytes());
            outputStream.close();
        } catch(Exception e) {
            return;
        }
    }
}
