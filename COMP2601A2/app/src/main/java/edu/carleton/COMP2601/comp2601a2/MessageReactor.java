package edu.carleton.COMP2601.comp2601a2;

import java.net.Socket;

public class MessageReactor {

    private static MessageReactor instance;

    private Socket s;
    private String userid;
    private EventStreamImpl es;

    /*----------
    - Description: connects the user to the server and prepares the client-side reactor
    - Input: IP address, port number, and username
    - Return: none
    ----------*/
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
                    GameActivity.getInstance().gameOn();
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
                    GameActivity.getInstance().gameOver(message);
                }
            });
            twr.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*----------
    - Description: converts a message to an event and sends it to the server
    - Input: the message to be sent
    - Return: none
    ----------*/
    public Message request(Message msg) {
        try {
            Event event = new Event(msg.header.type, es);
            event.put(Fields.ID, userid);
            event.put(Fields.PLAY_STATUS, msg.body.getField(Fields.PLAY_STATUS));
            if (!msg.body.getMap().isEmpty())
                event.put(Fields.BODY, msg.body.getMap());
            if (msg.body.getField(Fields.CHOICE) != null)
                event.put(Fields.CHOICE, msg.body.getField(Fields.CHOICE));
            if (msg.body.getField(Fields.WINNER) != null)
                event.put(Fields.WINNER, msg.body.getField(Fields.WINNER));
            event.put(Fields.RECIPIENT, msg.header.recipient);
            es.putEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    /*----------
    - Description: converts and event to a message and sends it to the client
    - Input: the event to be sent
    - Return: none
    ----------*/
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
        if (event.get(Fields.WINNER) != null)
            message.body.addField(Fields.WINNER, event.get(Fields.WINNER));
        message.header.id = event.get(Fields.ID).toString();
        return message;
    }

    public static MessageReactor getInstance() { return instance; }
}