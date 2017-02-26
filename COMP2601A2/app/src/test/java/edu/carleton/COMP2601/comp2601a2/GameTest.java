package edu.carleton.COMP2601.comp2601a2;

import org.junit.Test;

import static edu.carleton.COMP2601.comp2601a2.Game.EMPTY_VAL;
import static edu.carleton.COMP2601.comp2601a2.Game.X_VAL;
import static edu.carleton.COMP2601.comp2601a2.Game.O_VAL;
import static edu.carleton.COMP2601.comp2601a2.Game.TIE_WINNER;
import static org.junit.Assert.*;

public class GameTest {

    @Test
    public void makeMove() throws Exception {
        Game game = new Game();
        int choice = 0;
        int playerTurn = game.getPlayerTurn();

        game.makeMove(choice);
        assertEquals(playerTurn, game.getSquare(choice));
    }

    @Test
    public void gameWinner() throws Exception {

        Game game1 = new Game();
        game1.makeMove(0);
        game1.switchPlayer();
        game1.makeMove(1);
        game1.switchPlayer();
        game1.makeMove(5);
        game1.switchPlayer();
        game1.makeMove(3);
        game1.switchPlayer();
        game1.makeMove(2);
        game1.switchPlayer();
        game1.makeMove(4);
        game1.switchPlayer();
        game1.makeMove(6);
        game1.switchPlayer();
        game1.makeMove(7);
        assertEquals(O_VAL, game1.gameWinner());

        Game game2 = new Game();
        game2.makeMove(0);
        game2.switchPlayer();
        game2.makeMove(1);
        game2.switchPlayer();
        game2.makeMove(5);
        game2.switchPlayer();
        game2.makeMove(3);
        game2.switchPlayer();
        game2.makeMove(2);
        game2.switchPlayer();
        game2.makeMove(8);
        game2.switchPlayer();
        game2.makeMove(4);
        game2.switchPlayer();
        game2.makeMove(7);
        game2.switchPlayer();
        game2.makeMove(6);
        assertEquals(X_VAL, game2.gameWinner());

        Game game3 = new Game();
        game3.makeMove(0);
        game3.switchPlayer();
        game3.makeMove(1);
        game3.switchPlayer();
        game3.makeMove(5);
        game3.switchPlayer();
        game3.makeMove(3);
        game3.switchPlayer();
        game3.makeMove(2);
        game3.switchPlayer();
        game3.makeMove(4);
        game3.switchPlayer();
        game3.makeMove(6);
        game3.switchPlayer();
        game3.makeMove(8);
        game3.switchPlayer();
        game3.makeMove(7);
        assertEquals(TIE_WINNER, game3.gameWinner());

        Game game4 = new Game();
        game4.makeMove(0);
        game4.switchPlayer();
        game4.makeMove(1);
        game4.switchPlayer();
        game4.makeMove(5);
        game4.switchPlayer();
        game4.makeMove(3);
        game4.switchPlayer();
        game4.makeMove(2);
        game4.switchPlayer();
        game4.makeMove(4);
        game4.switchPlayer();
        game4.makeMove(6);
        game4.switchPlayer();
        game4.makeMove(8);
        assertEquals(EMPTY_VAL, game4.gameWinner());
    }

    @Test
    public void checkForRow() throws Exception {
        Game game = new Game();

        game.makeMove(0);
        game.makeMove(1);
        game.makeMove(2);
        assertEquals(true, game.checkForRow(0,1,2));

        game.makeMove(3);
        game.makeMove(4);
        game.switchPlayer();
        game.makeMove(5);
        assertEquals(false, game.checkForRow(3,4,5));
    }

    @Test
    public void switchPlayer() throws Exception {
        Game game = new Game();
        game.switchPlayer();
        assertEquals(O_VAL, game.getPlayerTurn());
        game.switchPlayer();
        assertEquals(X_VAL, game.getPlayerTurn());
    }

    @Test
    public void squareOccupied() throws Exception {
        Game game = new Game();
        game.makeMove(0);
        assertEquals(true, game.squareOccupied(0));
        assertEquals(false, game.squareOccupied(1));
    }
}