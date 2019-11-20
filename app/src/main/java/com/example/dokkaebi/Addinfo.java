package com.example.dokkaebi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.jar.Attributes;

public class Addinfo extends LinearLayout {
    CheckBox cc_user;
    LinearLayout ll_user;
    TextView tv_add,tv_port;
    int i =0;
    public Addinfo(Context context) {
        super(context);

        init(context);
    }
    public Addinfo(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }
    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.main_info_user,this,true);
    }
    @SuppressLint("ResourceType")
    public CheckBox getcb(){
        cc_user = findViewById(R.id.cc_user);
        return cc_user;
    }
    public LinearLayout getll(){
        ll_user = findViewById(R.id.ll_userinfo);
        return ll_user;
    }
    public TextView gettvad(){
        tv_add = findViewById(R.id.info_address);
        return tv_add;
    }
    public TextView gettvport(){
        tv_port = findViewById(R.id.info_port);
        return tv_port;
    }
}
