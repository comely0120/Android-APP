package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    TextView mTvBluetoothStatus;
    TextView mTvTimer;
    Button mBtnStart;
    Button mBtnBluetoothOn;
    Button mBtnBluetoothOff;
    Button mBtnConnect;
    Button mBtnDegree1;
    Button mBtnDegree2;
    Button mBtnDegree3;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    List<String> mListPairedDevices;
    Handler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;
    CountDownTimer mCountDown;
    Button btnplus,btnminous, btnOk;
    TextView WaterResult;


    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;  // MSG_DAY_PASSED
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    int CountTime = 0;
    int num;
    int result = 0;
    String readMessage;
    String readM;
    int stoc;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnStart = (Button)findViewById(R.id.btnStart);
        mTvBluetoothStatus = (TextView)findViewById(R.id.tvBluetoothStatus);
        mTvTimer=(TextView)findViewById(R.id.Timer);
        mBtnBluetoothOn = (Button)findViewById(R.id.btnBluetoothOn);
        mBtnBluetoothOff = (Button)findViewById(R.id.btnBluetoothOff);
        mBtnConnect = (Button)findViewById(R.id.btnConnect);
        mBtnDegree1 = (Button)findViewById(R.id.degree1);
        mBtnDegree2 = (Button)findViewById(R.id.degree2);
        mBtnDegree3 = (Button)findViewById(R.id.degree3);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btnplus = (Button)findViewById(R.id.btnplus);
        btnminous =(Button)findViewById(R.id.btnminous);
        btnOk =(Button)findViewById(R.id.btnOK);
        WaterResult = (TextView)findViewById(R.id.Water);




        mBtnBluetoothOn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothOn();
            }
        });
        mBtnBluetoothOff.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothOff();
            }
        });

        mBtnConnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                listPairedDevices();
            }
        });
        //50도
        mBtnDegree1.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                if(result !=0){
                    mBtnDegree1.setBackgroundColor(Color.rgb(251, 213, 188));
                    mThreadConnectedBluetooth.write("1");
                    for (int i = 1; i <= 10; i++) {
                        Toast.makeText(getApplicationContext(), "!!잠시 기다려주세요!! [ " + i + " ]", 1000).show();
                    }
                    Toast.makeText(getApplicationContext(), " START버튼을 눌러주세요 ", 1000).show();
                }else Toast.makeText(getApplicationContext(), "물 양을 먼저 선택하세요", Toast.LENGTH_LONG).show();
            }
        });
        //75도
        mBtnDegree2.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                if(result !=0){
                    mBtnDegree2.setBackgroundColor(Color.rgb(251, 213, 188));
                    mThreadConnectedBluetooth.write("2");
                    for (int i = 1; i <= 10; i++) {
                        Toast.makeText(getApplicationContext(), "!!잠시 기다려주세요!! [ " + i + " ]", 1000).show();
                    }
                    Toast.makeText(getApplicationContext(), " START버튼을 눌러주세요 ", 1000).show();
                }else Toast.makeText(getApplicationContext(), "물 양을 먼저 선택하세요", Toast.LENGTH_LONG).show();
            }
        });
        //100eh
        mBtnDegree3.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                if(result !=0){
                    mBtnDegree3.setBackgroundColor(Color.rgb(251, 213, 188));
                    mThreadConnectedBluetooth.write("3");
                    for (int i = 1; i <= 10; i++) {
                        Toast.makeText(getApplicationContext(), "!!잠시 기다려주세요!! [ " + i + " ]", 1000).show();
                    }
                    Toast.makeText(getApplicationContext(), " START버튼을 눌러주세요 ", 1000).show();
                }else Toast.makeText(getApplicationContext(), "물 양을 먼저 선택하세요", Toast.LENGTH_LONG).show();
            }
        });

        mBluetoothHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if(msg.what == BT_MESSAGE_READ) {
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


        //물양 더하기
        btnplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Plus();
                result = num;
                WaterResult.setText(result + "ml");

            }
        });
        //물양 빼기
        btnminous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Minous();
                result = num;
                WaterResult.setText(result+ "ml");
            }
        });
        //물양 선택버튼
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result != 0) {
                    btnOk.setBackgroundColor(Color.rgb(251, 213, 188));
                    btnOk.setTextColor(Color.rgb(254, 254, 254));
                }
                switch (result) {
                    case 200:
                        mThreadConnectedBluetooth.write("a");
                        break;
                    case 400:
                        mThreadConnectedBluetooth.write("b");
                        break;
                    case 600:
                        mThreadConnectedBluetooth.write("c");
                        break;
                    case 800:
                        mThreadConnectedBluetooth.write("d");
                        break;
                    case 1000:
                        mThreadConnectedBluetooth.write("e");
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "물 양을 선택하세요", Toast.LENGTH_LONG).show();

                }
            }
        });


        mBtnStart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                readM = readMessage.replaceAll("\r\n", "");
                readM = readM.trim();
                stoc = Integer.parseInt(readM);
                mThreadConnectedBluetooth.write("4");
                CountDownTimer();
            }


        });
    }

    public void onBackPressed() {
        AlertDialog.Builder alterDiaglogBuilder =new AlertDialog.Builder(context);
        alterDiaglogBuilder
                .setTitle("프로그램 종료");
        alterDiaglogBuilder.setMessage("프로그램을 종료할 것입니까?");
        alterDiaglogBuilder.setCancelable(false);
        alterDiaglogBuilder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.this.finish();
            }
        })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alterDiaglogBuilder.create();
        alert.show();
    }

    public void CountDownTimer(){
        CountTime = stoc * 1000;
        mCountDown = new CountDownTimer( CountTime, 1000){
            public void onTick(long millisUntilFinished){
                int min = CountTime/60000;
                int sec = (CountTime/1000)-min*60;
                mTvTimer.setText(min + "분" + sec +"초");
                CountTime= CountTime-1000;
                if(CountTime == 0){
                    createNotification();
                }
            }
            public void onFinish(){
                mTvTimer.setText("Done");
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(3000);
                mThreadConnectedBluetooth.write("5");
            }
        }.start();
    }

    private void createNotification() {
        PendingIntent mPendingIntent= PendingIntent.getActivity(MainActivity.this,0,
                new Intent(getApplicationContext(),MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher_port2);
        builder.setContentTitle("SMART ElETORIC PORT");
        builder.setContentText("!! 물 끓이기 완료 !!");
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setColor(Color.BLUE);
        builder.setContentIntent(mPendingIntent);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }


    public void onDestroy() {
        super.onDestroy();
        try {
            mCountDown.cancel();
        } catch (Exception e) {
        }
        mCountDown = null;
    }




    public Integer Plus(){
        num = num + 200;
        if(num >1000){
            Toast.makeText(getApplicationContext(), "물양을 초과했습니다", Toast.LENGTH_LONG).show();
            num = 0;
        }
        return(num);
    }
    public Integer Minous(){
        num = num - 200;
        if(num < 0){
            Toast.makeText(getApplicationContext(), "물 양을 선택하세요",Toast.LENGTH_LONG).show();
            num = 0;
        }
        return(num);
    }


    void bluetoothOn() {
        if(mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
        }
        else {
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "블루투스가 이미 활성화 되어 있습니다.", Toast.LENGTH_LONG).show();
                mTvBluetoothStatus.setText("활성화");
            }
            else {
                Toast.makeText(getApplicationContext(), "블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_LONG).show();
                Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE);
            }
        }
    }
    void bluetoothOff() {
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
            mTvBluetoothStatus.setText("비활성화");
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 이미 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                    Toast.makeText(getApplicationContext(), "블루투스 활성화", Toast.LENGTH_LONG).show();
                    mTvBluetoothStatus.setText("활성화");
                } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                    Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                    mTvBluetoothStatus.setText("비활성화");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    void listPairedDevices() {
        if (mBluetoothAdapter.isEnabled()) {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("장치 선택");

                mListPairedDevices = new ArrayList<String>();
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                    mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
                mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        connectSelectedDevice(items[item].toString());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    void connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;
                break;
            }
        }
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mBluetoothSocket.connect();
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
            //BT_CONNECTING_STATUS=3값을 핸들러에게 보냄
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_LONG).show();
        }
    }


    private class ConnectedBluetoothThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }


        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }




    }
}