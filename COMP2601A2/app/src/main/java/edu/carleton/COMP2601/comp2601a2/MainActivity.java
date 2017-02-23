package edu.carleton.COMP2601.comp2601a2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    static int X_VAL = 1, O_VAL = 2, TIE_WINNER = 3, EMPTY_VAL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
