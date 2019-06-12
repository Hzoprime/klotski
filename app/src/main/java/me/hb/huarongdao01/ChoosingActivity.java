package me.hb.huarongdao01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Locale;

public class ChoosingActivity extends Activity {
    final static String missionString = "mission";
    final static String buttonFormatString  = "第%d关";
    LinearLayout buttonsLayout;
    int missionNumber;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_choosing);
            buttonsLayout = findViewById(R.id.buttonsLayout);
            missionNumber = getResources().getInteger(R.integer.missionNumber);
            final ChoosingActivity self = this;
            for (int i = 0; i < missionNumber; i++) {
                final int order = i;
                Button button = new Button(this);
                button.setId(View.generateViewId());
                button.setText(String.format(Locale.SIMPLIFIED_CHINESE,buttonFormatString, i + 1));
                buttonsLayout.addView(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(self, GameActivity.class);
                        intent.putExtra(missionString, order);
                        startActivity(intent);
                    }
                });
            }
        }
}
