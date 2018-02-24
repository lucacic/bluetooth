package me.aflak.bluetoothterminal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;

import me.aflak.bluetooth.Bluetooth;

public class Chat extends AppCompatActivity implements Bluetooth.CommunicationCallback {
    private String name;
    private Bluetooth b;
    private Button b1;
    private Button b2;
    private Button b3;
    private Button b4;
    private Button b5;
    private boolean selected = false;

    private OutputStream out;


    private boolean registered=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        b1 = (Button)findViewById(R.id.button1);
        b2 = (Button)findViewById(R.id.button2);
        b3 = (Button)findViewById(R.id.button3);
        b4 = (Button)findViewById(R.id.button4);
        b5 = (Button)findViewById(R.id.button5);


        b1.setEnabled(false);
        b2.setEnabled(false);
        b3.setEnabled(false);
        b4.setEnabled(false);
        b5.setEnabled(false);

        b = new Bluetooth(this);
        b.enableBluetooth();


        b.setCommunicationCallback(this);

        int pos = getIntent().getExtras().getInt("pos");
        name = b.getPairedDevices().get(pos).getName();

        Log.d("Message", "Connecting...");
        //Display("Connecting...");
        b.connectToDevice(b.getPairedDevices().get(pos));

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendInt(out, 76);

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInt(out, 77);

                if(selected) {
                    //((ImageView) findViewById(R.id.imageViewLike)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.like_active_24dp));

                }else{

                    //((ImageView) findViewById(R.id.imageViewLike)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.like_active_24dp));
                }

            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInt(out, 78);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInt(out, 79);

            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInt(out, 80);

            }
        });

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        registered=true;
    }



    public void sendInt(OutputStream out, int value){
        try {
            out.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(registered) {
            unregisterReceiver(mReceiver);
            registered=false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.close:
                b.removeCommunicationCallback();
                b.disconnect();
                Intent intent = new Intent(this, Select.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    public void Display(final String s){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.append(s + "\n");
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }*/

    @Override
    public void onConnect(BluetoothDevice device) {
        Log.d("Message", "Connected to " + device.getName() + " - " + device.getAddress());
        //Display("Connected to "+device.getName()+" - "+device.getAddress());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                b1.setEnabled(true);
                b2.setEnabled(true);
                b3.setEnabled(true);
                b4.setEnabled(true);
                b5.setEnabled(true);
                try {
                    out = b.getSocket().getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {
        Log.d("Message", "Disconnected");
        Log.d("Message", "Connecting again");
        //Display("Disconnected!");
        //Display("Connecting again...")
        b.connectToDevice(device);
    }

    @Override
    public void onMessage(String message) {

        //Display(name+": "+message);
    }

    @Override
    public void onError(String message) {
        Log.d("Message Error: ", message);
        //Display("Error: "+message);
    }

    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        Log.d("Message Error: ", message);
        //Display("Error: "+message);
        //Display("Trying again in 3 sec.");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.connectToDevice(device);
                    }
                }, 2000);
            }
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Intent intent1 = new Intent(Chat.this, Select.class);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                }
            }
        }
    };


}
