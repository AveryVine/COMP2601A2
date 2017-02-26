package edu.carleton.COMP2601.comp2601a2;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;

import static edu.carleton.COMP2601.comp2601a2.Game.X_VAL;
import static edu.carleton.COMP2601.comp2601a2.Game.O_VAL;

public class MainActivity extends AppCompatActivity {

    private ProgressBar spinner;
    private String address;
    private int port = 7000;

    private android.widget.ArrayAdapter adapter;
    private static MainActivity instance;


    private ArrayList<String> array;

    MessageReactor messageReactor;
    private ListView listView;
    private String nameText = "";
    private TextView textField;


    /*----------
    - Description: runs when the activity first boots up
    ----------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.main_activity_name);

        address = "192.168.0.21";
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        textField = (TextView) findViewById(R.id.textView);
        spinner.setVisibility(View.GONE);
        promptName();

        messageReactor = new MessageReactor();
        array = new ArrayList<String>();
        instance = this;


        adapter = new ArrayAdapter<String>(this, R.layout.activity_main_component, array);

        listView = (ListView) findViewById(R.id.list);
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
                if (!name.equals(nameText)) {
                    Message message = new Message();
                    message.header.type = "PLAY_GAME_REQUEST";
                    message.header.recipient = name;
                    messageReactor.request(message);
                }
                else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            textField.setText("You cannot challenge yourself to a game.");
                        }
                    });
                }
            }
        });

    }

    /*----------
    - Description: Sends a disconnect request message to the server
    - Input: none
    - Return: none
    ----------*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Message message = new Message();
        message.header.type = "DISCONNECT_REQUEST";
        messageReactor.request(message);
    }


    /*----------
    - Description: UI updates here to show that user is connected
    - Input: none
    - Return: none
    ----------*/
    public void connectedResponse() {
        runOnUiThread(new Runnable() {
            public void run() {
                spinner.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*----------
    - Description: Option given to receiver of game request message to
                    join the game (yes) or not to (no) in the form of a Dialog.
    - Input: mes
    - Return: none
    ----------*/
    public void playGameRequest(final Message mes) {
        runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
                builder.setTitle(mes.header.id + " has challenged you to a game.");

                // Set up the buttons
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // PLAY_GAME_RESPONSE is returned to the server with the play status set to true. A new GameActivity display is then created.
                        Message message = new Message();
                        message.header.type = "PLAY_GAME_RESPONSE";
                        message.header.recipient = mes.header.id;
                        message.body.addField(Fields.PLAY_STATUS, "true");
                        messageReactor.request(message);
                        Intent gameActivity = new Intent(MainActivity.getInstance(), GameActivity.class);
                        gameActivity.putExtra("playerTurn", O_VAL);
                        gameActivity.putExtra("player", nameText);
                        gameActivity.putExtra("opponent", mes.header.id);
                        MainActivity.getInstance().startActivity(gameActivity);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //PLAY_GAME_RESPONSE is returned to the server with the play status set to false.
                        Message message = new Message();
                        message.header.type = "PLAY_GAME_RESPONSE";
                        message.header.recipient = mes.header.id;
                        message.body.addField(Fields.PLAY_STATUS, "false");
                        messageReactor.request(message);
                    }
                });
                builder.show();
            }
        });
    }

    /*----------
    - Description: Function starts the game activity if other player said YES,
                    and updates the textfield if other player said NO.
    - Input: mes
    - Return: none
    ----------*/
    public void playGameResponse(final Message mes) {
        if (mes.body.getField(Fields.PLAY_STATUS).toString().equals("true")) {
            Intent gameActivity = new Intent(this, GameActivity.class);
            gameActivity.putExtra("playerTurn", X_VAL);
            gameActivity.putExtra("player", nameText);
            gameActivity.putExtra("opponent", mes.header.id);
            MainActivity.getInstance().startActivity(gameActivity);
        }
        else {
            runOnUiThread(new Runnable() {
                public void run() {
                    textField.setText(mes.header.id + " does not want to play.");
                }
            });
        }
    }


    /*----------
    - Description: Dialog for retrieving users name on connection.
    - Input: none
    - Return: none
    ----------*/
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

    /*----------
    - Description: Updates array of active users in the application.
    - Input: mes
    - Return: none
    ----------*/
    public void usersUpdated(Message mes) {
        try {
            JSONObject jsonObj = new JSONObject(mes.body.getField(Fields.BODY).toString());
            JSONArray jsonArr = (JSONArray) jsonObj.get("listOfUsers");
            array.clear();
            for (int i = 0; i < jsonArr.length(); i++) {
                System.out.println("Added : " + jsonArr.get(i).toString());
                System.out.println(jsonArr.get(i).toString().getClass().getName());
                array.add(jsonArr.get(i).toString());
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

    //Main activity single instance
    public static MainActivity getInstance() { return instance; }
}
