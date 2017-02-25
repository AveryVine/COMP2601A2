package edu.carleton.COMP2601.comp2601a2;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    static int X_VAL = 1, O_VAL = 2, TIE_WINNER = 3, EMPTY_VAL = 0;
    private ProgressBar spinner;

    private String address = "192.168.0.21";
    private int port = 7000;

    private android.widget.ArrayAdapter adapter;
    public static MainActivity instance;


    ArrayList<String> array;

    MessageReactor messageReactor;
    private String nameText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        promptName();

        messageReactor = new MessageReactor();
        array = new ArrayList<String>();
        instance = this;


        adapter = new ArrayAdapter<String>(this,
                R.layout.activity_main_component, array);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    Message mes = new Message();
                    mes.header.type = "CONNECT_REQUEST";
                    while (nameText == "") {

                    }
                    messageReactor.connect(address, port, nameText);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            spinner.setVisibility(View.VISIBLE);
                        }
                    });
                    messageReactor.request(mes);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String name = array.get(position);
                Message gameRequestMes = new Message();
                gameRequestMes.header.type = "PLAY_GAME_REQUEST";
                gameRequestMes.body.addField(Fields.RECIPIENT, name);
                messageReactor.request(gameRequestMes);
            }
        });

    }

    public static MainActivity getInstance() { return instance; }


    public void connectedResponse() {
        //TODO - Add above (and remove here) progress spinner
        runOnUiThread(new Runnable() {
            public void run() {
                spinner.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void playGameRequest(Message mes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set up the buttons
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // PLAY_GAME_RESPONSE is returned to the server
                // with the play status set to true. A new GameActivity display is then created.
                

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //PLAY_GAME_RESPONSE is returned to the server with the play status set to false.
            }
        });
        builder.show();
    }


    public void promptName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter your name here");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nameText = input.getText().toString();
            }
        });
        builder.show();
    }

    public void usersUpdated(Message mes) {
        try {
            JSONObject jsonObj = new JSONObject(mes.body.getField("listOfUsers").toString());
            for (int i = 0; i < jsonObj.length(); i++) {
                array.add(((JSONArray) jsonObj.get("listOfUsers")).get(i).toString());
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
