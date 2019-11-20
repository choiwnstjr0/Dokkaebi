package com.example.dokkaebi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Setting extends AppCompatActivity   {
    private int touch_num,vibrate_num = 1;
    NumberPicker touch_np,vibrate_np;
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.setting);
            vibrate_np = findViewById(R.id.n_vibrate);
            touch_np = findViewById(R.id.n_touch);
            Button submit_btn = findViewById(R.id.set_submit_btn);

            vibrate_np.setMinValue(0);
            vibrate_np.setMaxValue(4);
            vibrate_np.setValue(0);
            touch_np.setMinValue(1);
            touch_np.setMaxValue(4);
            touch_np.setValue(1);
            submit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touch_num = touch_np.getValue();
                    vibrate_num = vibrate_np.getValue();
                    Toast.makeText(Setting.this, "설정되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });

        }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("vibrate_num",vibrate_num);
        intent.putExtra("touch_num",touch_num);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        super.onDestroy();
    }
}
