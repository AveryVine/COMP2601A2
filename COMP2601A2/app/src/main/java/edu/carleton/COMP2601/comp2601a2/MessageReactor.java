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
                    Message message = convertEventToMessage(event);
                    MainActivity.getInstance().usersUpdated(message);
                }
            });
            twr.register("PLAY_GAME_REQUEST", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("Received PLAY_GAME_REQUEST");
                    Message message = convertEventToMessage(event);
                    MainActivity.getInstance().playGameRequest(message);
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
            event.put(Fields.RECIPIENT, msg.body.getField(Fields.RECIPIENT));
            if (!msg.body.getMap().isEmpty()) {
                event.put(Fields.BODY, msg.body.getMap());
            }
            es.putEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    public Message convertEventToMessage(Event event) {
        Message message = new Message();
        message.header.type = event.type;
        if (event.get(Fields.BODY) != null)
            message.body.addField(Fields.BODY, event.get(Fields.BODY));
        if (event.get(Fields.RECIPIENT) != null)
            message.body.addField(Fields.RECIPIENT, event.get(Fields.RECIPIENT));
        message.header.id = event.get(Fields.ID).toString();
        return message;
    }
}