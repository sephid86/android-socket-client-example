package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler;
    private Socket socket;
    private String ip = "10.0.2.2"; // IP 주소
    private int port = 8282; // PORT번호

    Button Button01;
    EditText et;
    TextView msgTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.EditText01);
        mHandler = new Handler();
        final TextView tv = (TextView) findViewById(R.id.TextView01);
        msgTV = (TextView)findViewById(R.id.chatTV);

        Thread sock =new SockThread();
        sock.start();
    }

    class SockThread extends Thread {
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(ip);
                socket =  new Socket(serverAddr,port);

            }catch (Exception e){
                e.printStackTrace();
            }
            Button01 = (Button)findViewById(R.id.Button01);
            Button01.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view)
                {
                    Thread send = new SendThread();
                    send.start();
                    //Toast.makeText(this, "버튼이 눌렸어요.", Toast.LENGTH_LONG).show();
                }
            });
            Thread recv = new RecvThread();
            recv.start();
        }

    }
    class SendThread extends Thread {
        @Override
        public void run() {
            try {
                String sndMsg = et.getText().toString();
                Log.d("send===========", sndMsg);
                //데이터 전송
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                out.printf(sndMsg);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    class RecvThread extends Thread {
        @Override
        public void run() {
            while(1>0) {
                try {
                    InputStream stream = socket.getInputStream();
                    byte[] data = new byte[4096];

                    stream.read(data);

                    mHandler.post(new msgUpdate(new String(data)));
                    Log.d("recv=============", new String(data));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

    class msgUpdate implements Runnable {
        private String msg;
        public msgUpdate(String str) {
            this.msg = str;
        }
        public void run() {
            msgTV.setText(msgTV.getText().toString() + msg);
        }
    }

}

