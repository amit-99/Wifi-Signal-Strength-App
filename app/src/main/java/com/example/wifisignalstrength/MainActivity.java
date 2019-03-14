package com.example.wifisignalstrength;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button btnInfo;
    TextView txtWifiInfo;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtWifiInfo = (TextView) findViewById(R.id.idTxt);
        btnInfo = (Button)findViewById(R.id.idBtn);
        btnSave = (Button)findViewById(R.id.idBtn1);

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWifiInformation();

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                savetofile();

            }
        });

        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                getWifiInformation();
                savetofile();
                handler.postDelayed(this, 2000);
            }
        };

        handler.postDelayed(r, 1000);
    }

    public void getWifiInformation(){
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo  = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String macAddress = wifiInfo.getMacAddress();
        String bssid =  wifiInfo.getBSSID();
        int rssi = wifiInfo.getRssi();
        int linkspeed = wifiInfo.getLinkSpeed();
        String ssid = wifiInfo.getSSID();
        int networkId = wifiInfo.getNetworkId();
        int level = WifiManager.calculateSignalLevel(rssi,5);
        String ipAddress = Formatter.formatIpAddress(ip);

        String info="";
        if(networkId==-1){
            info = "Sorry you are not connected to the WIFI" +
                    "\n" + "Connect to Wifi and then try again!!";
        }
        else{
            info = "IP Address : " + ipAddress +
                    "\n" + "Mac Address : " + macAddress +
                    "\n" + "BSSID : " + bssid +
                    "\n" + "SSID : " + ssid +
                    "\n" + "rssi : " + rssi + " (in dbm)" +
                    "\n" + "NetworkID : " + networkId +
                    "\n" + "Level : " + level;
        }
        txtWifiInfo.setText(info);
    }

    public void savetofile(){

        File directory = new File(Environment.getExternalStorageDirectory() + java.io.File.separator +"WSS");
        if (!directory.exists())
            Toast.makeText(this, (directory.mkdirs() ? "Directory has been created" : "Directory not created"), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "File Updated", Toast.LENGTH_SHORT).show();
        System.out.println(directory);
        File file = new File(Environment.getExternalStorageDirectory() + java.io.File.separator +"WSS" + java.io.File.separator + "logs.txt");
        System.out.println(file);

        Date currentTime = Calendar.getInstance().getTime();
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        try {
            OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(file,true));
            BufferedWriter buffered_writer = new BufferedWriter(file_writer);
            buffered_writer.write("\n\n"+currentTime+"\n"+txtWifiInfo.getText().toString()+"\n");
            buffered_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
