package edu.carleton.COMP2601.comp2601a2;

import java.net.Socket;

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
                    Message message = convertEventToMessage(event);
                    MainActivity.getInstance().playGameResponse(message);
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
            event.put(Fields.PLAY_STATUS, msg.body.getField(Fields.PLAY_STATUS));
            if (!msg.body.getMap().isEmpty())
                event.put(Fields.BODY, msg.body.getMap());
            event.put(Fields.RECIPIENT, msg.body.getField(Fields.RECIPIENT));
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
            message.header.recipient = event.get(Fields.RECIPIENT).toString();
        if (event.get(Fields.PLAY_STATUS) != null)
            message.body.addField(Fields.PLAY_STATUS, event.get(Fields.PLAY_STATUS));
        message.header.id = event.get(Fields.ID).toString();
        return message;
    }
}