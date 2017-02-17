package com.example.pipob.carrinho;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pipob on 10/01/2017.
 */

public class WebSocketNode {
    WebSocketClient mWebSocketClient ;
    long delay=0;
    String lastMessage="";

    List<String> queueMessage= new ArrayList<String>();
    Thread runMessage;
    Boolean running = false;

    public void sendMessages(){
            try {

                if (running) {
                    if (!queueMessage.isEmpty() && mWebSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN) {
                        if (running){ 
                            if(!lastMessage.equals(queueMessage.get(0)))
                            mWebSocketClient.send(queueMessage.get(0));
                            Log.v("Message",queueMessage.get(0));
                            lastMessage = queueMessage.get(0);
                            queueMessage.remove(0);
                        }
                    }
                    try {
                        runMessage.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                }catch(Exception e){
                    e.printStackTrace();
                }


    }

    public void writeMessage(String message){
        queueMessage.add(message);
    }


    public WebSocket.READYSTATE getState(){
          return mWebSocketClient.getReadyState();
    }


    public WebSocketNode(String ip){
        //COMEÇO DE CODIGO WEBSOCKET
        URI uri;
        try {
            uri = new URI("ws://" + ip);
            Log.v("URL",uri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }


        mWebSocketClient = new WebSocketClient(uri,new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened " + mWebSocketClient.getReadyState());
                running=true;
            }

            @Override
            public void onMessage(String s) {
                final String str =s ;
                new Runnable() {
                    @Override
                    public void run() {
                        Log.i("MessageFromWebSocket",str);
                    }
                }.run();


            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }


            @Override
            public void onClosing(int code, String reason, boolean remote) {
                super.onClosing(code, reason, remote);
                Log.i("Websocket","Closing");

            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }

        };

        Runnable runMessages = new Runnable() {
            @Override
            public void run() {
                while(true)
                sendMessages();
            }
        };
        runMessage  = new Thread(runMessages);
        runMessage.start();
   }

    public void connect(){
        try {
            mWebSocketClient.connect();

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void close(){
        try{
            try{
                running=false;
            runMessage.interrupt();}catch(Exception e){e.printStackTrace();}
            mWebSocketClient.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }


    }


}
