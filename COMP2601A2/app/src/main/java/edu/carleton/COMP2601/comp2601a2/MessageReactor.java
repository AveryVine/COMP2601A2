package edu.carleton.COMP2601.comp2601a2;

import java.net.Socket;

public class MessageReactor {

    private static MessageReactor instance;

    private Socket s;
    private String userid;
    private EventStreamImpl es;

    public void connect(String host, int port, String userid) {
        instance = this;
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
                    Message message = convertEventToMessage(event);
                    GameActivity.getInstance().gameOn(message);
                }
            });
            twr.register("MOVE_MESSAGE", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("Received MOVE_MESSAGE");
                    Message message = convertEventToMessage(event);
                    GameActivity.getInstance().moveMessage(message);
                }
            });
            twr.register("GAME_OVER", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("Received GAME_OVER");
                    Message message = convertEventToMessage(event);
                    //GameActivity.getInstance().moveMessage(message);
                }
            });
            twr.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Message request(Message msg) {
        try {
            Event event = new Event(msg.header.type, es);
            event.put(Fields.ID, userid);
            event.put(Fields.PLAY_STATUS, msg.body.getField(Fields.PLAY_STATUS));
            if (!msg.body.getMap().isEmpty())
                event.put(Fields.BODY, msg.body.getMap());
            if (msg.body.getField(Fields.CHOICE) != null)
                event.put(Fields.CHOICE, msg.body.getField(Fields.CHOICE));
            event.put(Fields.RECIPIENT, msg.header.recipient);
            System.out.println("Recipient: " + event.get(Fields.RECIPIENT));
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
        if (event.get(Fields.CHOICE) != null)
            message.body.addField(Fields.CHOICE, event.get(Fields.CHOICE));
        message.header.id = event.get(Fields.ID).toString();
        return message;
    }

    public static MessageReactor getInstance() {
        return instance;
    }
}