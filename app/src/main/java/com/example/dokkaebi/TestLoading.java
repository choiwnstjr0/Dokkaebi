package com.example.dokkaebi;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TestLoading extends AppCompatActivity {
    SocketManager manager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testloading);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        manager = SocketManager.getInstance();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();

    }
}
