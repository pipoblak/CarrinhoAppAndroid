package com.example.pipob.carrinho;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.jmedeisis.bugstick.Joystick;
import com.jmedeisis.bugstick.JoystickListener;

import org.java_websocket.WebSocket;

public class MainActivity extends AppCompatActivity {
    WebSocketNode webSocketNode;
    String IPCarrinho = "192.168.0.34:81";
    Button btnCon;
    ProgressBar progressBar;
    Thread verifyCon ;
    Joystick joystick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joystick = (Joystick) findViewById(R.id.joystick);
        btnCon = (Button) findViewById(R.id.btnConnect);
        webSocketNode = new WebSocketNode(IPCarrinho);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        verifyCon = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    asyncStatus();
                    try {
                        verifyCon.sleep(300);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }

            }
        });
        verifyCon.start();


        btnCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnConnect:

                        if (btnCon.getText().equals("Conectar")) {
                            webSocketNode = new WebSocketNode(IPCarrinho);
                            webSocketNode.connect();


                        } else if (btnCon.getText().equals("Desconectar")) {
                            webSocketNode.close();
                        }
                        break;
                }
            }
        });





        joystick.setJoystickListener(new JoystickListener() {
            @Override
            public void onDown() {

            }

            @Override
            public void onDrag(float degrees, float offset) {
                if(degrees<0){

                }
                else{
                    if(degrees>60 && degrees <125){
                        webSocketNode.writeMessage("R"+1000 + "L"+ 1000);
                    }
                    else if(degrees<60){
                        webSocketNode.writeMessage("R"+500 + "L"+ 1000);
                    }
                    else if(degrees>125){
                        webSocketNode.writeMessage("R"+1000 + "L"+ 500);
                    }
                }

            }

            @Override
            public void onUp() {
                webSocketNode.writeMessage("R"+0 + "L"+ 0);

            }
        });
    }

    //ASYNC DE VERIFICAÇÃO DE CONEXAO
    public void asyncStatus(){
        new AsyncTask<Void, String, Void>() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Void... params) {

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if(webSocketNode.getState() == WebSocket.READYSTATE.CONNECTING){
                    btnCon.setText("Conectando...");
                    btnCon.setClickable(false);
                    progressBar.setVisibility(View.VISIBLE);
                    joystick.setVisibility(View.INVISIBLE);

                }
                else if (webSocketNode.getState() == WebSocket.READYSTATE.NOT_YET_CONNECTED){
                    btnCon.setText("Conectar");
                    btnCon.setClickable(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    joystick.setVisibility(View.INVISIBLE);

                }
                else if (webSocketNode.getState() == WebSocket.READYSTATE.CLOSED){
                    btnCon.setText("Conectar");
                    btnCon.setClickable(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    joystick.setVisibility(View.INVISIBLE);

                }
                else if (webSocketNode.getState() == WebSocket.READYSTATE.CLOSING){
                    btnCon.setText("Desconectando...");
                    btnCon.setClickable(false);
                    progressBar.setVisibility(View.VISIBLE);
                    joystick.setVisibility(View.INVISIBLE);
                }
                else if (webSocketNode.getState() == WebSocket.READYSTATE.OPEN){
                    btnCon.setText("Desconectar");
                    btnCon.setClickable(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    joystick.setVisibility(View.VISIBLE);

                }

            }

            @Override
            protected void onProgressUpdate(String... values) {

            }
        }.execute();

    }
}
