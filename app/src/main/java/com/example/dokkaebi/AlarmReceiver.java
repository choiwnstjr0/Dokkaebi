package com.example.dokkaebi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d("alarmtest","dd");
        Toast.makeText(context, "alarm", Toast.LENGTH_LONG).show();
        Intent gointent = new Intent(context,Hacking.class);
        this.context.startActivity(gointent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        //메인으로 보내면 메인에 있는 상황에서는 인텐트가 하나가 더쌓여버린다.
        // throw new UnsupportedOperationException("Not yet implemented");
    }
}
