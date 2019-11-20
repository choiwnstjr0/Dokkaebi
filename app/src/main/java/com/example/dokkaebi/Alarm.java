package com.example.dokkaebi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Alarm extends AppCompatActivity {
    NumberPicker hourf,hours,minf,mins;
    int hour,min = 0;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        hourf = findViewById(R.id.hour_f);
        hours =findViewById(R.id.hour_s);
        minf = findViewById(R.id.min_f);
        mins = findViewById(R.id.min_s);
        Button submit = findViewById(R.id.alarm_submt);

        hourf.setMinValue(0);hourf.setMaxValue(2);hourf.setValue(1);
        hours.setMinValue(0);hours.setMaxValue(9);hours.setValue(1);
        minf.setMinValue(0);minf.setValue(1);minf.setMaxValue(5);
        mins.setMinValue(0);;mins.setValue(1);mins.setMaxValue(9);
        hourf.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                if(newVal < 2){
                    hours.setMaxValue(9);
                    minf.setMaxValue(5);
                    mins.setMaxValue(9);
                }
                if(newVal == 2) {
                    hours.setMaxValue(4);
                    if(hours.getValue() == 4){
                        minf.setMaxValue(0);
                        mins.setMaxValue(0);
                    }
                    Toast.makeText(Alarm.this, "bb", Toast.LENGTH_SHORT).show();

                    hours.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            if (hourf.getValue() == 2 &&newVal == 4) {
                                Toast.makeText(Alarm.this, "aa", Toast.LENGTH_SHORT).show();
                                minf.setMaxValue(0);
                                mins.setMaxValue(0);
                            } else {
                                minf.setMaxValue(5);
                                mins.setMaxValue(9);
                            }
                        }
                    });
                }
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hour = hourf.getValue()*10 + hours.getValue();
                min = minf.getValue()*10 + mins.getValue();
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                //mCalendar.set(Calendar.HOUR_OF_DAY,0);
                mCalendar.set(Calendar.MINUTE,min);
                mCalendar.set(Calendar.SECOND,1);
                Toast.makeText(Alarm.this, ""+min, Toast.LENGTH_SHORT).show();
                //default 시간은 시스템에서 시간을 받아와 현재시간을 기준으로 알람맞추게끔유도
                //만약 시스템 시간보다 설정한 시간이 적다면 다음날에 알람이 설정되도록 한다.
                //아니면 월화수목금토일 로 설정할 수 있도록(이것도 전날이라면 일주일을 더 더해야한다.)
                Intent mAlarmIntent = new Intent(getApplicationContext(),AlarmReceiver.class);
                mAlarmIntent.setAction("com.example.dokkaebi.ALARM_START");
                PendingIntent mPendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(),
                        1001,
                        mAlarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        mCalendar.getTimeInMillis(),
                        mPendingIntent
                );
                Toast.makeText(Alarm.this, "aa", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
