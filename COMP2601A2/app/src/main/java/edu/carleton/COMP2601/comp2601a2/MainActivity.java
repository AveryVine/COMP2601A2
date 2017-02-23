package edu.carleton.COMP2601.comp2601a2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
                    Message listFiles = new Message();
                    listFiles.header.type = "list files";
                    System.out.println("Lookup: " + listFiles);

                    //MainActivity.getInstance().s.request(listFiles);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static MainActivity getInstance() { return instance; }


    public void updateListView(Message mes) {

        for (int i=1; i<3; i++) {
            array.add(mes.body.getField(Integer.toString(i)).toString());
        }
        adapter.notifyDataSetChanged();
    }
}
