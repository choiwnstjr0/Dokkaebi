package com.example.dokkaebi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Hacking extends AppCompatActivity {
    private ImageView dok_img1,dok_img2;
    private boolean imgcon = true , img_sw = true;
    Vibrator vibrator;
    long [] pattern = {230,380,230,1550};
    int count = 0;
    int vibnum = 0,touchnum = 1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hacking);

        dok_img1 = findViewById(R.id.dok_img1);
        dok_img2 = findViewById(R.id.dok_img2);
        Intent intent = getIntent();
        vibnum = intent.getIntExtra("vib_num",0);
        touchnum = intent.getIntExtra("touch_num",1);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vitr();
    }
    public void vitr(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(vibnum == 0) vibrator.vibrate(pattern,0);
                else {
                    for (int i = 0; i < vibnum; i++) {
                        vibrator.vibrate(pattern, -1);
                        try {
                            Thread.sleep(2400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    vibrator.cancel();
                    finish();
                }
            }
        }).start();
    }

        Handler handler = new Handler() {
            @Override
          public void handleMessage(Message msg){
              updateThread();
          }
        };
        protected void onStart() {
            super.onStart();
            Thread imgthread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (imgcon) {

                        if(count == touchnum) break;
                        try {
                            handler.sendMessage(handler.obtainMessage());
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    finish();
                }
            });
            imgthread.start();
        }

        private void updateThread(){
            if(img_sw) {
                dok_img1.setVisibility(View.VISIBLE);
                dok_img2.setVisibility(View.INVISIBLE);
                img_sw = false;
            }
            else{
                dok_img1.setVisibility(View.INVISIBLE);
                dok_img2.setVisibility(View.VISIBLE);
                img_sw = true;
            }
        }
        public boolean onTouchEvent(MotionEvent event){
            int action = event.getAction();

            switch(action){
                case MotionEvent.ACTION_DOWN:
                    count++;
                    break;
            }

            return super.onTouchEvent(event);
        }

    @Override
    protected void onDestroy() {
            imgcon = false;
           vibrator.cancel();
        super.onDestroy();

    }

}


