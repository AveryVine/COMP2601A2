package edu.carleton.COMP2601.comp2601a2;
import java.util.Random;

public class Game {

    static final int X_VAL = 1, O_VAL = 2, TIE_WINNER = 3, EMPTY_VAL = 0;

    private int[] board;
    private int playerTurn;
    private boolean active;

    /*----------
    - Description: constructor for game
    ----------*/
    public Game(int playerTurn) {
        active = true;
        this.playerTurn = playerTurn;
        board = new int[9];
    }

    /*----------
    - Description: select a random UNOCCUPIED square to make a move in
    - Input: none
    - Return: the selected square
    ----------*/
    public int randomSquare() {
        Random rand = new Random();
        int choice = -1;
        do {
            choice = rand.nextInt(9);
        } while (board[choice] != EMPTY_VAL);
        return choice;
    }

    /*----------
    - Description: makes a move for the current player at the given square
    - Input: choice of square
    - Return: none
    ----------*/
    public void makeMove(int choice) {
        board[choice] = playerTurn;
    }

    /*----------
    - Description: checks to see if anyone has won the game, or if the game has resulted in a tie
    - Input: none
    - Return: 1 (X wins), 2 (O wins), 3 (tie), or 0 (game not over)
    ----------*/
    public int gameWinner() {
        if (checkForRow(0, 1, 2)
                || checkForRow(3, 4, 5)
                || checkForRow(6, 7, 8)
                || checkForRow(0, 3, 6)
                || checkForRow(1, 4, 7)
                || checkForRow(2, 5, 8)
                || checkForRow(0, 4, 8)
                || checkForRow(2, 4, 6))
            return playerTurn;
        for (int i = 0; i < 9; i++) {
            if (board[i] == EMPTY_VAL) { return EMPTY_VAL; }
        }
        return TIE_WINNER;
    }

    /*----------
    - Description: checks to see if the provided row is complete
    - Input: the three squares in question
    - Return: complete or not complete
    ----------*/
    public boolean checkForRow(int square1, int square2, int square3) {
        if (board[square1] == board[square2]
                && board[square1] == board[square3]
                && board[square1] != EMPTY_VAL)
            return true;
        return false;
    }

    /*----------
    - Description: switches the active player
    - Input: none
    - Return: none
    ----------*/
    public void switchPlayer() {
        playerTurn = (playerTurn == X_VAL ? O_VAL : X_VAL);
    }

    /*----------
    - Description: checks to see if the provided square is occupied
    - Input: the square to check
    - Return: occupied or not occupied
    ----------*/
    public boolean squareOccupied(int square) {
        return (board[square] == EMPTY_VAL) ? false : true;
    }

    /*----------
    - Description: getters
    ----------*/
    public int getPlayerTurn() { return playerTurn; }
    public boolean getActive() { return active; }
    public void toggleActive() { active = !active; }
    public int getSquare(int choice) {return board[choice]; }

    /*----------
    - Description: prints the current state of the board
    ----------*/
    public void print() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i * 3 + j] + "\t");
            }
            System.out.println();
        }
    }

}
