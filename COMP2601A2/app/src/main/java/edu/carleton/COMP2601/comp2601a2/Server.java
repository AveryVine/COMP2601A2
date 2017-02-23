package edu.carleton.COMP2601.comp2601a2;

import java.io.*;
import java.net.*;
import java.util.*;
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
        r.register("connection", new EventHandler() {
            public void handleEvent(Event ev) {
                System.out.println("Received connection request");
                String id = (String) ev.get(Fields.ID);
                System.out.println("Client: " + id);
                ThreadWithReactor twr = (ThreadWithReactor) Thread.currentThread();
                clients.put(id, twr);
                try {
                    Event e = new Event("loginSuccess");
                    e.put(Fields.ID, id);
                    EventStream es = twr.getEventSource();
                    es.putEvent(e);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        r.register("message", new EventHandler() {
            public void handleEvent(Event ev) {
                System.out.println("Received message request");
                try {
                    String recipient = (String) ev.get(Fields.RECIPIENT);
                    ThreadWithReactor twr = clients.get(recipient);
                    EventStream es = twr.getEventSource();
                    System.out.println("Recipient: " + recipient);
                    System.out.println("Contents: " + ev.get(Fields.BODY));
                    es.putEvent(ev);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
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

    public void run() {
        try {
            while (true) {
                s = listener.accept();
                System.out.println("Accepted a connection");
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
