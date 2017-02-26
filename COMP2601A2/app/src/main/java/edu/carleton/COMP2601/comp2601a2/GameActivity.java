package edu.carleton.COMP2601.comp2601a2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import static edu.carleton.COMP2601.comp2601a2.Game.X_VAL;
import static edu.carleton.COMP2601.comp2601a2.Game.O_VAL;
import static edu.carleton.COMP2601.comp2601a2.Game.TIE_WINNER;
import static edu.carleton.COMP2601.comp2601a2.Game.EMPTY_VAL;

public class GameActivity extends AppCompatActivity {

    public static GameActivity instance;

    private Game game;
    private Button startButton;
    private TextView displayTextView;
    private ImageButton[] imgButtonArr;
    private int playerTurn;
    private String player, opponent;
    private MessageReactor messageReactor;

    /*----------
    - Description: runs when the application first boots up
    ----------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        instance = this;
        messageReactor = MessageReactor.getInstance();

        imgButtonArr = new ImageButton[9];

        playerTurn = (int) getIntent().getSerializableExtra("playerTurn");
        player = (String) getIntent().getSerializableExtra("player");
        opponent = (String) getIntent().getSerializableExtra("opponent");

        initUI();
        game = new Game();
        game.toggleActive();

        startButton.setOnClickListener(new View.OnClickListener() {
            /*----------
            - Description: runs when the start/running button is pressed; toggles the state of the game
            ----------*/
            public void onClick(View view) {
                if (game.getActive()) {
                    game.toggleActive();
                    gameOverUI(EMPTY_VAL);
                    toggleClickListeners(false);
                }
                else if (playerTurn == X_VAL) {
                    game = new Game();
                    prepareUI();
                    Message message = new Message();
                    message.header.type = "GAME_ON";
                    message.header.recipient = opponent;
                    messageReactor.request(message);
                    toggleClickListeners(true);
                }
            }
        });
    }

    /*----------
    - Description: links UI elements in activity_main.xml to their programmatic equivalents in this file
    - Input: none
    - Return: none
    ----------*/
    public void initUI() {
        for (int i = 0; i < 9; i++) {
            String imgButtonID = "imageButton" + i; //This is not in strings.xml as it is used for resource ID's
            int resourceID = getResources().getIdentifier(imgButtonID, "id", getPackageName());
            imgButtonArr[i] = (ImageButton) findViewById(resourceID);
        }
        displayTextView = (TextView) findViewById(R.id.textView);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setText(getString(R.string.startButton_gameInactive));
        displayTextView.setText(getString(R.string.displayTextView_gameInactive));
    }

    /*----------
    - Description: prepares the UI for the start of a new game
    - Input: none
    - Return: none
    ----------*/
    public void prepareUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                startButton.setText(getString(R.string.startButton_gameActive));
                displayTextView.setText(getString(R.string.blank));
                for (int i = 0; i < 9; i++) {
                    updateSquareUI(imgButtonArr[i], EMPTY_VAL);
                }
            }
        });
    }

    /*----------
    - Description: toggles on and off user input on the board
    - Input: none
    - Return: none
    ----------*/
    public void toggleClickListeners(boolean toggleOn) {
        for (int i = 0; i < 9; i++) {
            if (toggleOn) {
                if (!game.squareOccupied(i)) {
                    imgButtonArr[i].setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            squareClicked(view);
                        }
                    });
                }
            }
            else {
                imgButtonArr[i].setOnClickListener(null);
            }
        }
    }


    public void gameOn(Message message) {
        prepareUI();
    }

    /*----------
    - Description: updates the UI of the text view after a move has been made
    - Input: the move that was made
    - Return: none
    ----------*/
    public void updateDisplayTextView(int choice) {
        if (choice == 0) displayTextView.setText(getString(R.string.square0));
        else if (choice == 1) displayTextView.setText(getString(R.string.square1));
        else if (choice == 2) displayTextView.setText(getString(R.string.square2));
        else if (choice == 3) displayTextView.setText(getString(R.string.square3));
        else if (choice == 4) displayTextView.setText(getString(R.string.square4));
        else if (choice == 5) displayTextView.setText(getString(R.string.square5));
        else if (choice == 6) displayTextView.setText(getString(R.string.square6));
        else if (choice == 7) displayTextView.setText(getString(R.string.square7));
        else if (choice == 8) displayTextView.setText(getString(R.string.square8));
        else displayTextView.setText(getString(R.string.blank));
    }

    /*----------
    - Description: makes a move for the user when they click a square on the board
    - Input: the ImageButton that was clicked
    - Return: none
    ----------*/
    public void squareClicked(View view) {
        int choice = -1;
        for (int j = 0; j < 9; j++) {
            String imgButtonID = "imageButton" + j; //This is not in strings.xml as it is used for resource ID's
            int resourceID = getResources().getIdentifier(imgButtonID, "id", getPackageName());
            if (view.getId() == resourceID) {
                choice = j;
            }
        }
        game.makeMove(choice);
        updateSquareUI(imgButtonArr[choice], game.getPlayerTurn());
        updateDisplayTextView(choice);
        Message message = new Message();
        message.header.type = "MOVE_MESSAGE";
        message.header.recipient = opponent;
        message.body.addField(Fields.CHOICE, choice);
        messageReactor.request(message);
        toggleClickListeners(false);
        game.switchPlayer();
    }

    /*----------
    - Description: updates the UI of a specific square on the board after a move has been made
    - Input: the ImageButton to be updated, the value to put inside the square
    - Return: none
    ----------*/
    public void updateSquareUI(final ImageButton imgButton, final int value) {
        switch (value) {
            case X_VAL:
                imgButton.setImageResource(R.drawable.x_button);
                break;
            case O_VAL:
                imgButton.setImageResource(R.drawable.o_button);
                break;
            default:
                imgButton.setImageResource(R.drawable.empty_button);
        }
    }

    /*----------
    - Description: updates the UI after the game has been won or the game has ended
    - Input: the winner
    - Return: none
    ----------*/
    public void gameOverUI(final int winner) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (winner == playerTurn) {
                    displayTextView.setText(getString(R.string.gameOver_part1) + " " + player + " " + getString(R.string.gameOver_part2));
                }
                else if (winner != playerTurn) {
                    displayTextView.setText(getString(R.string.gameOver_part1) + " " + opponent + " " + getString(R.string.gameOver_part2));
                }
                else if (winner == TIE_WINNER) {
                    displayTextView.setText(R.string.tie_winner);
                }
                else {
                    displayTextView.setText(R.string.no_winner);
                }
                startButton.setText(getString(R.string.startButton_gameInactive));
            }
        });
    }

    
    public void moveMessage(Message mes) {
        System.out.println("Player Turn: " + game.getPlayerTurn());
        final int choice = Integer.parseInt(mes.body.getField(Fields.CHOICE).toString());
        game.makeMove(choice);

        /*----------
        - These next few lines ensure that the UI is updated AFTER the state of the game board is updated
        ----------*/
        final Runnable updateUI = new Runnable() {
            /*----------
            - Description: runs when the user has made a move; updates the UI squares
            ----------*/
            @Override
            public void run() {
                updateSquareUI(imgButtonArr[choice], game.getPlayerTurn());
                updateDisplayTextView(choice);
                synchronized (this) {
                    this.notify();
                }
            }
        };
        synchronized (updateUI) {
            runOnUiThread(updateUI);
            waitToUpdateUI(updateUI);
        }
        /*----------
        ----------*/

        int gameWinner = game.gameWinner();
        if (gameWinner == EMPTY_VAL) {
            game.switchPlayer();
            toggleClickListeners(true);
        }
        else {
            game.toggleActive();
            Message message = new Message();
            message.header.type = "GAME_OVER";
            message.header.recipient = opponent;
            message.body.addField(Fields.WINNER, gameWinner);
            messageReactor.request(message);
            gameOverUI(gameWinner);
        }
    }

    public void gameOver(Message message) {
        gameOverUI(Integer.parseInt(message.body.getField(Fields.WINNER).toString()));
    }

    /*----------
    - Description: ensures that the game board is up to date before updating the UI
    - Input: the runnable causing the UI of the board to update
    - Return: none
    ----------*/
    public void waitToUpdateUI(Runnable updateUI) {
        try {
            updateUI.wait();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GameActivity getInstance() {
        return instance;
    }
}
