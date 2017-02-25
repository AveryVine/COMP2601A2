package edu.carleton.COMP2601.comp2601a2;

import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.net.Socket;
import java.util.Map;

/**
 * Created by AveryVine on 2017-02-23.
 */

public class MessageReactor {

    private Socket s;
    private String userid;
    private EventStreamImpl es;

    public void connect(String host, int port, String userid) {
        this.userid = userid;
        try {
            s = new Socket(host, port);
            System.out.println("Connected (address " + host + ", port " + port + ")");
            es = new EventStreamImpl(s.getOutputStream(), s.getInputStream());
            ThreadWithReactor twr = new ThreadWithReactor(es);
            twr.register("CONNECTED_RESPONSE", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("Received CONNECTED_RESPONSE");
                    MainActivity.getInstance().connectedResponse();
                }
            });
            twr.register("USERS_UPDATED", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("Received USERS_UPDATED");
                    Message message = new Message();
                    message.header.type = event.type;
                    if (event.get(Fields.BODY) != null)
                        message.body.addField("listOfUsers", event.get(Fields.BODY));
                    message.header.id = event.get(Fields.ID).toString();
                    MainActivity.getInstance().usersUpdated(message);
                }
            });
            twr.register("PLAY_GAME_REQUEST", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("Received PLAY_GAME_REQUEST");
                }
            });
            twr.register("PLAY_GAME_RESPONSE", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("Received PLAY_GAME_RESPONSE");
                }
            });
            twr.register("DISCONNECT_RESPONSE", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("Received DISCONNECT_RESPONSE");
                }
            });
            twr.register("GAME_ON", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("Received GAME_ON");
                }
            });
            twr.register("MOVE_MESSAGE", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("Received MOVE_MESSAGE");
                }
            });
            twr.register("GAME_OVER", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("Received GAME_OVER");
                }
            });
            twr.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Message request(Message msg) {
        try {
            System.out.println("Requesting...");
            Event event = new Event(msg.header.type, es);
            event.put(Fields.ID, userid);
            event.put(Fields.RECIPIENT, msg.header.recipient);
            if (!msg.body.getMap().isEmpty()) {
                event.put(Fields.BODY, msg.body.getMap());
            }
            es.putEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

//    public void broadcast(Serializable obj) {
//        // This will cause all BroadcastReceivers to receive
//        // the intent message. We‘d put obj here as payload.
//        Intent i = new Intent();
//        i.putExtra("myKey", obj);
//        i.setAction(CUSTOM_INTENT);
//        System.out.println("Object: " + obj);
//        sendBroadcast(i);
//    }
}
