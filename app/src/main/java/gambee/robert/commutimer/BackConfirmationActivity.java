package gambee.robert.commutimer;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class BackConfirmationActivity extends AppCompatActivity {
    public void onBackPressed(boolean showPopup, CoordinatorLayout parent) {
        if (!showPopup) {
            super.onBackPressed();
            return;
        }
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
        popupLayout.setBackgroundColor(getColor(R.color.colorPopup));

        TextView popupMessage = new TextView(this);
        popupMessage.setText(getString(R.string.back_popup_message));
        popupLayout.addView(popupMessage);

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setLayoutParams(params);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        final Button yesButton = new Button(this);
        yesButton.setText(getString(R.string.back_popup_yes));
        final Button noButton = new Button(this);
        noButton.setText(getString(R.string.back_popup_no));

        buttonLayout.addView(yesButton);
        buttonLayout.addView(noButton);
        popupLayout.addView(buttonLayout);

        final PopupWindow popup = new PopupWindow(popupLayout, WRAP_CONTENT, WRAP_CONTENT, true);
        popup.showAtLocation(parent, Gravity.CENTER, 0, 0);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                BackConfirmationActivity.super.onBackPressed();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }
}
