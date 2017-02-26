package edu.carleton.COMP2601.comp2601a2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    static final int X_VAL = 1, O_VAL = 2, TIE_WINNER = 3, EMPTY_VAL = 0;

    private Game game;
    private Button startButton;
    private TextView displayTextView;
    private ImageButton[] imgButtonArr;

    /*----------
    - Description: runs when the application first boots up
    ----------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgButtonArr = new ImageButton[9];

        //initUI();
        game = new Game();
        game.toggleActive();

        startButton.setOnClickListener(new View.OnClickListener() {
            /*----------
            - Description: runs when the start/running button is pressed; toggles the state of the game
            ----------*/
            public void onClick(View view) {
                if (game.getActive()) {
                    gameThread.interrupt();
                    gameThread = null;
                    game.toggleActive();
                    gameOverUI(EMPTY_VAL);
                    toggleClickListeners();
                }
                else {
                    game = new Game();
                    prepareUI();
                    toggleClickListeners();
                    gameLoop();
                    initGameThread();
                    gameThread.start();
                }
            }
        });
    }

    public void gameLoop() {
        while (game.getActive()) {
            
        }
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
        startButton = (Button) findViewById(R.id.button);
        startButton.setText(getString(R.string.startButton_gameInactive));
        displayTextView.setText(getString(R.string.displayTextView_gameInactive));
    }

    /*----------
    - Description: prepares the UI for the start of a new game
    - Input: none
    - Return: none
    ----------*/
    public void prepareUI() {
        startButton.setText(getString(R.string.startButton_gameActive));
        displayTextView.setText(getString(R.string.blank));
        for (int i = 0; i < 9; i++) {
            updateSquareUI(imgButtonArr[i], EMPTY_VAL);
        }
    }

    /*----------
    - Description: toggles on and off user input on the board
    - Input: none
    - Return: none
    ----------*/
    public void toggleClickListeners() {
        for (int i = 0; i < 9; i++) {
            if (game.getPlayerTurn() == X_VAL) {
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

    /*----------
    - Description: updates the UI of a specific square on the board after a move has been made
    - Input: the ImageButton to be updated, the value to put inside the square
    - Return: none
    ----------*/
    public void updateSquareUI(ImageButton imgButton, int value) {
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
    public void gameOverUI(int winner) {
        if (winner == X_VAL) { displayTextView.setText(R.string.x_winner); }
        else if (winner == O_VAL) { displayTextView.setText(R.string.o_winner); }
        else if (winner == TIE_WINNER) { displayTextView.setText(R.string.tie_winner); }
        else { displayTextView.setText(R.string.no_winner); }
        startButton.setText(getString(R.string.startButton_gameInactive));
    }
}
