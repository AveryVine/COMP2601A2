package edu.carleton.COMP2601.comp2601a2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static int X_VAL = 1, O_VAL = 2, TIE_WINNER = 3, EMPTY_VAL = 0;

    private String address;
    private int port;

    private android.widget.ArrayAdapter adapter;
    public static MainActivity instance;


    ArrayList<String> array;

    MessageReactor messageReactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        array = new ArrayList<String>();
        instance = this;

        adapter = new ArrayAdapter<String>(this,
                R.layout.activity_main, array);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);


        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    Message mes = new Message();
                    mes.header.type = "CONNECT_REQUEST";

                    messageReactor.connect(address, port, nameText);

                    messageReactor.request(mes);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static MainActivity getInstance() { return instance; }

    public void connectedResponse() {
        Toast.makeText(MainActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
        //TODO - Add above (and remove here) progress spinner
    }

    public void usersUpdated(Message mes) {

        JSONObject json;
        try {
            json = new JSONObject(mes.body.getField("listOfUsers").toString());
            array = (ArrayList) json.get("listOfUsers");
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
