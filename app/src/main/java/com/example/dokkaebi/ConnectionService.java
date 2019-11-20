package com.example.dokkaebi;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class ConnectionService extends Service {
    final int STATUS_DISCONNECTED = 0;
    final int STATUS_CONNECTED = 1;
    final int TIME_OUT = 5000;

    int vibnum =0, touchnum = 0;
    private int status = STATUS_DISCONNECTED;
    private Socket socket;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;
    private SocketAddress socketAddress;
    private int port = 22223;

    IConnectionService.Stub binder = new IConnectionService.Stub() {
        @Override
        public int getStatus() throws RemoteException {
            return status;
        }

        @Override
        public void setSocket(String ip) throws RemoteException {
            mySetScoekt(ip);
        }

        @Override
        public void connect() throws RemoteException {
            myConnect();
        }

        @Override
        public void disconnect() throws RemoteException {
                myDisconnect();
        }

        @Override
        public void send(String msg) throws RemoteException {
            mySend(msg);
        }

        @Override
        public void receive() throws RemoteException {
                myReceive();
        }
    };
    public ConnectionService(){

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public boolean onUnbind(Intent intent){
        return super.onUnbind(intent);
    }
    void mySetScoekt(String ip){
        socketAddress = new InetSocketAddress(ip,port);
    }

    void myConnect(){
        new Thread(new Runnable() {
            @SuppressLint("LongLogTag")
            @Override
            public void run() {
                try {
//                    socket = new Socket("121.169.212.177",8080);
//                    socket = new Socket("lymaru.duckdns.org",22223);
                   socket = new Socket();

                    socketAddress = new InetSocketAddress("lymaru.duckdns.org",22223);
//                    socketAddress = new InetSocketAddress("172.28.1.239",8080);
                   socket.connect(socketAddress,TIME_OUT);
                   Log.i("C_Socket_port",""+socket.getLocalPort());

                   writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                   reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    status = STATUS_CONNECTED;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    void setBinder(IBinder binder){

    }
    void myDisconnect(){

    }


    void mySend(String msg){
        line = msg;
        new Thread(new Runnable() {
            @Override
            public void run() {

//                String msg = "{ \"SocketID\" : \"ID\", \"Address\" : \"Address\"}\n";
                try {
                    writer.write(line,0,line.length());
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    ReadMsg rm = ReadMsg.getInstance();
    String line = "null";
    void myReceive(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){
                    try {
                            line = reader.readLine();
                            Log.d("socket_readline",line);
                            if(line!=null) {
                                rm.setline(line);
                                JSONObject json = new JSONObject(line);
                                if(json.has("touch")) {
                                    Message msg = Message.obtain(handler, 0, 0);
                                    handler.sendMessage(msg);
                                }
                            }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        rm.setcheck(0);

    }
    public void hackjson() throws JSONException {
        JSONObject json = new JSONObject((line));
        if(json.has("touch")){
            touchnum = json.getInt("touch");
            vibnum = json.getInt("vibration");
        }

    }
    public Handler handler = new Handler(){
      public void handleMessage(Message msg){
          try {
              hackjson();
          } catch (JSONException e) {
              e.printStackTrace();
          }
          Intent intent = new Intent(getApplicationContext(),Hacking.class);
          intent.putExtra("touch_num",touchnum);
          intent.putExtra("vib_num",vibnum);
          startActivity(intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK));

      }
    };

}
