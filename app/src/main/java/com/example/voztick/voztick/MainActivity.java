package com.example.voztick.voztick;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

@TargetApi(Build.VERSION_CODES.ECLAIR)
public class MainActivity extends AppCompatActivity {
    private OutputStream outputStream;
    final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice device = null;
    BluetoothSocket socket = null;
    final int bluetoothRequest = 1;
    final int bluetoothPair = 2;
    boolean connect = false;
    private static String MAC = null;
    UUID meuUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    Button bluetoothBtn;

    ImageButton mic ;
    int result;
    //Socket socket;
    final int recordCode=0;
    private TextView command;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (shouldAskPermissions()) {
            askPermissions();
        }
        command= (TextView) findViewById(R.id.commandText);
        mic=(ImageButton) findViewById(R.id.micBtn);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpeechRec();
            }
        });


        bluetoothBtn = (Button) findViewById(R.id.bluetoothButton);
        if (bluetooth == null) {
            Toast.makeText(getApplicationContext(), "Seu dispositivo não suporta bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }
        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //      .setAction("Action", null).show();
        else {
            if (!bluetooth.isEnabled()) {
                Intent ativaBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(ativaBluetooth, bluetoothRequest);
            }
        }
        bluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connect) {
                    //disconnect
                    try {
                        socket.close();
                      //  bluetoothBtn.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        connect = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //connect
                    Intent abreLista = new Intent(MainActivity.this, DeviceList.class);
                    startActivityForResult(abreLista, bluetoothPair);
                }


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){

            case bluetoothRequest:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth Ativado", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "O Bluetooth NÃO Foi Ativado, encerrando a aplicação", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case bluetoothPair:
                if (resultCode == RESULT_OK) {
                    MAC = data.getExtras().getString(DeviceList.MAC);
                    //  Toast.makeText(getApplicationContext(),"MAC: "+MAC,Toast.LENGTH_LONG).show();
                    device = bluetooth.getRemoteDevice(MAC);
                    try {
                        socket = device.createRfcommSocketToServiceRecord(meuUUID);
                        socket.connect();
                        outputStream = socket.getOutputStream();
                        Toast.makeText(getApplicationContext(), "Conectado com: " + MAC, Toast.LENGTH_LONG).show();
                        connect = true;
                        //bluetoothBtn.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro \n " + e, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Conexão não estabelecida", Toast.LENGTH_LONG).show();

                }
            break;
            case recordCode:
                if(resultCode==RecognizerIntent.RESULT_NO_MATCH||data==null){
                    SpeechRec();
                }
                if(resultCode==RESULT_OK && data!= null){
                    ArrayList<String> texto = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    command.setText(texto.get(0));
                    /*URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        url = new URL("http://192.168.4.1/led/on");
                        urlConnection = (HttpURLConnection) url
                                .openConnection();

                        InputStream in = urlConnection.getInputStream();

                        InputStreamReader isw = new InputStreamReader(in);

                        int dat = isw.read();
                        while (dat != -1) {
                            char current = (char) dat;
                            dat = isw.read();
                            System.out.print(current);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }*/

                    if(texto.get(0).contains("ula")||texto.get(0).contains("aixa")||texto.get(0).contains("direit")||texto.get(0).contains("esquerd")){
                        command.setText("");
                    String  r[] =   texto.get(0).split(" ");
                        for(int i=0; i<r.length;i++){
                            if(r[i].contains("ula")){
                                if (connect) {
                                    try {
                                        outputStream.write("p".getBytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                command.setText(command.getText()+"p");
                            }else if(r[i].contains("aix")){
                                if (connect) {
                                    try {
                                        outputStream.write("a".getBytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                command.setText(command.getText()+"a");
                            }else if(r[i].contains("direit")){
                                if (connect) {
                                    try {
                                        outputStream.write("d".getBytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                command.setText(command.getText()+"d");
                            }else if(r[i].contains("esquerd")){
                                if (connect) {
                                    try {
                                        outputStream.write("e".getBytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                command.setText(command.getText()+"e");
                            }
                        }
                    }

                   /* if(texto.get(0).contains("ula")){
                        //new GetMethodDemo().execute("http://192.168.4.1/led/red/on");
                        command.setText(command.getText()+" comando1");
                        if (connect) {
//                    while (rightButton.isPressed()){
                            try {

                                outputStream.write("p".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }//}

                        }

                    }
                   else if(texto.get(0).contains("aix")){
                        //new GetMethodDemo().execute("http://192.168.4.1/led/red/off");
                        command.setText(command.getText()+" comando2");
                        if (connect) {
//                    while (rightButton.isPressed()){
                            try {

                                outputStream.write("a".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }//}

                        }

                    }
                   else if(texto.get(0).contains("esquerd")){
                        //new GetMethodDemo().execute("http://192.168.4.1/led/yellow/on");
                        command.setText(command.getText()+" comando3");
                        if (connect) {
//                    while (rightButton.isPressed()){
                            try {

                                outputStream.write("e".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }//}

                        }

                    }
                   else if(texto.get(0).equals("desligar 2")||texto.get(0).equals("desligar dois")||texto.get(0).contains("direit")){
                        //new GetMethodDemo().execute("http://192.168.4.1/led/yellow/off");
                        command.setText(command.getText()+" comando4");
                        if (connect) {
//                    while (rightButton.isPressed()){
                            try {

                                outputStream.write("d".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }//}

                        }

                    }
*/              SpeechRec();
                }
                break;

        }
    }
    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

   void SpeechRec(){
       Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
       i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
       i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
       i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something");
       i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1);
       i.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true);
       try {
           startActivityForResult(i, recordCode);
       }catch (ActivityNotFoundException e){
           Toast.makeText(MainActivity.this,"Seu dispositivo não suporta a conversão de voz em texto",Toast.LENGTH_LONG).show();
       }
   }
}