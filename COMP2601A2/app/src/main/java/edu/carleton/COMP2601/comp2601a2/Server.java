package edu.carleton.COMP2601.comp2601a2;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    public static int PORT = 7000;
    private ServerSocket listener;
    private Socket s;
    private Reactor r;
    private EventStreamImpl es;
    private ThreadWithReactor twr;
    private ConcurrentHashMap<String, ThreadWithReactor> clients;

    public void init() {
        r = new Reactor();
        clients = new ConcurrentHashMap<String, ThreadWithReactor>();

        r.register("CONNECT_REQUEST", new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                System.out.println("Received CONNECT_REQUEST");
                String id = (String) event.get(Fields.ID);
                System.out.println("Client Connected: " + id);
                ThreadWithReactor twr = (ThreadWithReactor) Thread.currentThread();
                clients.put(id, twr);

                Event ev = new Event("CONNECTED_RESPONSE");
                EventStream es = twr.getEventSource();
                try {
                    es.putEvent(ev);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                sendUpdatedUserList();
            }
        });
        r.register("PLAY_GAME_REQUEST", new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                System.out.println("Received PLAY_GAME_REQUEST");
                passEventToRecipient(event);
            }
        });
        r.register("PLAY_GAME_RESPONSE", new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                System.out.println("Received PLAY_GAME_RESPONSE");
                passEventToRecipient(event);
            }
        });
        r.register("DISCONNECT_REQUEST", new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                System.out.println("Received DISCONNECT_REQUEST");
                String id = (String) event.get(Fields.ID);
                System.out.println("Client Disconnected: " + id);
                clients.remove(id);
                sendUpdatedUserList();
            }
        });
        r.register("GAME_ON", new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                System.out.println("Received GAME_ON");
                passEventToRecipient(event);
            }
        });
        r.register("MOVE_MESSAGE", new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                System.out.println("Received MOVE_MESSAGE");
                passEventToRecipient(event);
            }
        });
        r.register("GAME_OVER", new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                System.out.println("Received GAME_OVER");
                passEventToRecipient(event);
            }
        });

        try {
            listener = new ServerSocket(PORT);
            run();
            s.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void passEventToRecipient(Event event) {
        ThreadWithReactor twr = clients.get(event.get(Fields.RECIPIENT));
        EventStream es = twr.getEventSource();
        try {
            es.putEvent(event);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUpdatedUserList() {
        Event event = new Event("USERS_UPDATED");
        ArrayList<String> listOfClients = new ArrayList<>();
        for (ConcurrentHashMap.Entry<String, ThreadWithReactor> entry : clients.entrySet()) {
            listOfClients.add(entry.getKey());
        }

        JSONObject object = new JSONObject();
        try {
            object.put("listOfUsers", listOfClients);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        event.put(Fields.BODY, object.toString());

        for (ConcurrentHashMap.Entry<String, ThreadWithReactor> entry : clients.entrySet()) {
            twr = clients.get(entry.getKey());
            EventStream es = twr.getEventSource();
            try {
                es.putEvent(event);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        try {
            while (true) {
                System.out.println("Listening...");
                s = listener.accept();
                es = new EventStreamImpl(s);
                twr = new ThreadWithReactor(es, r);
                twr.start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server ns = new Server();
        ns.init();
    }
}
