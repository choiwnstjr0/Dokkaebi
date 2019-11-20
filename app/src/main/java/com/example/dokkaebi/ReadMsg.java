package com.example.dokkaebi;

import android.util.Log;

public class ReadMsg {
    String msg;
    int i = 0;
    private static final ReadMsg ourInstance = new ReadMsg();

    public static ReadMsg getInstance() {
        return ourInstance;
    }

    private ReadMsg() {
    }
    public void setline (String line){
        Log.d("line",""+line);
        this.msg = line;
    }
    String getMsg(){
        return msg;
    }
    public void setcheck(int i){
        this.i = i;
    }

}
