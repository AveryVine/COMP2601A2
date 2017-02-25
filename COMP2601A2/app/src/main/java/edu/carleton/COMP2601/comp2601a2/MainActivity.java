package edu.carleton.COMP2601.comp2601a2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static int X_VAL = 1, O_VAL = 2, TIE_WINNER = 3, EMPTY_VAL = 0;


    private android.widget.ArrayAdapter adapter;
    public static MainActivity instance;


    ArrayList<String> array;
    static ObjectInputStream ois;
    static ObjectOutputStream oos;

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
                    mes.header.type = "CONNECT_REQUEST";;
                    System.out.println("Lookup: " + mes);
                    s.request(mes);

                    messageReactor.connect();

                    //MainActivity.getInstance().s.request(listFiles);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static MainActivity getInstance() { return instance; }


    public void updateListView(Message mes) {

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
