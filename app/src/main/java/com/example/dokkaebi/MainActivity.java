package com.example.dokkaebi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    String line; //ReadMsg 에서 가져온 메시지 저장용도
    SocketManager manager = null;
    ReadMsg rm = ReadMsg.getInstance(); //read thread에서 온 메시지 읽는 single패턴 가져오기
    int num=0;
    int vibnum = 0; //진동 횟수
    int touchnum = 1; //터치 횟수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton refreshbtn = (ImageButton) findViewById(R.id.refresh);
        Button setbtn = (Button) findViewById(R.id.setbtn);
        Button alarm = (Button) findViewById(R.id.alarm);
        Button hackbtn = (Button) findViewById(R.id.hackbtn);

        manager = SocketManager.getInstance();
        //소켓 연결
        try {
            manager.connect();
            Thread.sleep(2000);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        info_pars();
        //refresh버튼
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                info_pars();

            }
        });

        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setting.class);
                startActivity(intent);
            }
        });
        //알람 버튼
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("ResourceType")
                Intent intent = new Intent(getApplicationContext(),Alarm.class);
                startActivity(intent);


            }
        });
        //해킹 버튼
        hackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "{\"HACKING\" :[";
                boolean fcheck = false;//first check 첫번째라면 json 오브젝트안 배열끝에 ","가 들어가지 않게 하기 위한 check boolean이다.
                for (int i = 0; i < num; i++) {
                    CheckBox cb = findViewById(0x7f100000 + i); //동적으로 만들어진 체크박스
                    TextView porttv = findViewById(0x7f400000 + i); // 동적으로 만들어진 Port text view

                    if (cb.isChecked()) {
                        if (fcheck) msg += ",";
                        fcheck = true;
                        String strvib = Integer.toString(vibnum);
                        String strtouch = Integer.toString(touchnum);
                        msg += "{\"port\" : \"" + porttv.getText() + "\",\"vibration\":" + strvib + ",\"touch\":" + strtouch + "}";

                    }
                }
                msg += "]}\n";
                Log.d("msg", "" + msg);
                Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_SHORT).show();
                if (fcheck)
                    try {
                        manager.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

            }
        });
    }
//셋팅에서 넘겨준 진동 횟수 값과 터치 횟수 값을 가져오기 위하여 onNewIntent를 선언
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        vibnum = intent.getIntExtra("vibrate_num",0);
        touchnum = intent.getIntExtra("touch_num",0);
        Toast.makeText(getApplicationContext(), "vib = "+vibnum + "touch = " + touchnum, Toast.LENGTH_SHORT).show();
    }
//onDestroy 에서 메소드를 모두 끈다.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Debug.stopMethodTracing();
    }
    //동적으로 서버에서 받아온 정보들을 레이아웃으로 만들어주는
    CheckBox cb;
    LinearLayout ll;
    private void info_pars(){

        //서버에 값을 요청한다.
        try {
            manager.send("{\"REFRESH\" : 0}\n");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
            try {
                manager.receive();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        //잠시 받아오기위한 sleep
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //메시지를 받아온다.
        line =  rm.getMsg();
        if(rm.getMsg() != null)
            Toast.makeText(getApplicationContext(), ""+rm.getMsg(), Toast.LENGTH_SHORT).show();
        //json 파싱을 시작한다.
        JSONArray jsonarray = null;
        try {

            JSONObject jsonobj = new JSONObject(line);
            if(jsonobj == null) Log.d("jsonobj","isnull");
            jsonarray =jsonobj.getJSONArray("REFRESH");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout ll_bord = null;
        Addinfo add = null;
        if(jsonarray == null) Log.d("jsonarray","isnull");
        else {
            ll_bord = (LinearLayout) findViewById(R.id.ll_bord);
            ll_bord.removeAllViews();//다시 새로 불러올때 레이아웃에 있는 정보들을 모두 지운후 다시 동적으로 생성해준다.
            num = jsonarray.length();
            for (int i = 0; i < jsonarray.length(); i++) {
                try {
                    JSONObject tmp = (JSONObject) jsonarray.get(i);
                    add  = new Addinfo(getApplicationContext());
                    ll_bord.addView(add);
                    add.getcb().setId(0x7f100000 + i);//체크박스 주소 지정 cc_user
                    add.getll().setId(0x7f200000 + i);//레이아웃 주소 지정 ll_userinfo
                    add.gettvad().setId(0x7f300000 + i);// 텍스트 뷰 (주소) 주소 지정 tv_address
                    add.gettvport().setId(0x7f400000 + i);//텍스트 뷰 (포트) 주소 지정 tv_port
                    cb = (CheckBox) findViewById(0x7f100000 + i);
                    ll = (LinearLayout) findViewById(0x7f200000 + i);
                    @SuppressLint("ResourceType")
//                    final CheckBox cb = (CheckBox) findViewById(0x7f100000 + i);
//                    final LinearLayout ll = (LinearLayout) findViewById(0x7f200000 + i);
                    final TextView tvadd = findViewById(0x7f300000 + i);
                    final TextView tvport = findViewById(0x7f400000 + i);


                    tvadd.setText((String) tmp.get("address"));
                    tvport.setText((String) tmp.get("port"));

                    //ll_userinfo 터치를 하면 체크 박스에 체크 할 수있는 이벤트
                    ll.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            int action = event.getAction();
                            if (action == event.ACTION_DOWN) {

                                if (!cb.isChecked())
                                    cb.setChecked(true);

                                else
                                    cb.setChecked(false);
                            }

                            return false;
                        }
                    });
                    //체크박스 체크 여부에 따른 색상 지정
                    cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                ll.setBackgroundColor(Color.parseColor("#8B4513"));


                            } else {
                                ll.setBackgroundColor(Color.parseColor("#00000F"));
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
