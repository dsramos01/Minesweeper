package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //private static final int COLUMN_COUNT = 2;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;

    private int seconds = 0;
    private boolean running;
    private boolean digMode = true;
    private int flagCount = 4;
    private TextView numFlags;
    private boolean gameLose = false;
    private boolean gameWin = false;
    private int count = 0;


    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runTimer();
        cell_tvs = new ArrayList<TextView>();
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);

        numFlags = findViewById(R.id.NumberFlags);

        TextView gameButton = findViewById(R.id.textViewPick);
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameButton.getText().toString().equals(getResources().getString(R.string.pick))) {
                    gameButton.setText(R.string.flag);
                    digMode = false;
                }
                else {
                    gameButton.setText(R.string.pick);
                    digMode = true;
                }
            }
        });

        // Create grid of text views
        LayoutInflater li = LayoutInflater.from(this);
        for (int i = 0; i<=9; i++) {
            for (int j=0; j<=7; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                //tv.setText(String.valueOf(i)+String.valueOf(j));
                tv.setText("0");
                tv.setTextColor(Color.GRAY);
                tv.setHint("");
//                tv.setHintTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.GRAY);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }
        Random rand = new Random();
        // Make sure there's always four bombs and no two bombs fall in the same cell
        ArrayList<Integer> list = new ArrayList<>();
        list.add(0);
        for (int j=0;j<4;j++) {
            int currInt = rand.nextInt(80);

            for (int k : list) {
                while (k == currInt) {
                    currInt = rand.nextInt(80);
                }
            }
            list.add(currInt);

            TextView currCell = cell_tvs.get(currInt);
            currCell.setHint(R.string.mine);
            currCell.setText("-1");
            // If bomb is on top left
            if (currInt == 0) {
                TextView newCell = cell_tvs.get(currInt + 1);
                addBombCount(newCell);
                newCell = cell_tvs.get(currInt + 8);
                addBombCount(newCell);
                newCell = cell_tvs.get(currInt + 9);
                addBombCount(newCell);
            }
            // If bomb is on top right
            else if (currInt == 7) {
                TextView newCell = cell_tvs.get(currInt - 1);
                addBombCount(newCell);
                newCell = cell_tvs.get(currInt + 7);
                addBombCount(newCell);
                newCell = cell_tvs.get(currInt + 8);
                addBombCount(newCell);
            }
            // If bomb is on bottom left
            else if (currInt == 72) {
                TextView newCell = cell_tvs.get(currInt - 8);
                addBombCount(newCell);
                newCell = cell_tvs.get(currInt - 7);
                addBombCount(newCell);
                newCell = cell_tvs.get(currInt + 1);
                addBombCount(newCell);
            }
            // If bomb is on bottom right
            else if (currInt == 79) {
                TextView newCell = cell_tvs.get(currInt - 9);
                addBombCount(newCell);
                newCell = cell_tvs.get(currInt - 8);
                addBombCount(newCell);
                newCell = cell_tvs.get(currInt - 1);
                addBombCount(newCell);
            }
            // If bomb is on top edge (no corners)
            else if (currInt > 0 & currInt < 7) {
                TextView newCell = cell_tvs.get(currInt - 1);
                addBombCount(newCell);
                newCell = cell_tvs.get(currInt + 1);
                addBombCount(newCell);
                // Do math to find three adjacent cells below
                for (int i=7;i<10;i++) {
                    newCell = cell_tvs.get(currInt + i);
                    addBombCount(newCell);
                }
            }
            // If bomb is on left edge (no corners)
            else if (currInt % 8 == 0) {
                TextView newCell = cell_tvs.get(currInt - 8);
                addBombCount(newCell);
                newCell = cell_tvs.get(currInt + 8);
                addBombCount(newCell);
                // Do math to find three adjacent cells on the right
                for (int i=-8;i<=8;i+=8) {
                    newCell = cell_tvs.get(currInt + i + 1);
                    addBombCount(newCell);
                }
            }
            //If bomb is on right edge (no corners)
            else if (currInt % 8 == 7) {
                TextView newCell = cell_tvs.get(currInt - 8);
                addBombCount(newCell);
                newCell = cell_tvs.get(currInt + 8);
                addBombCount(newCell);
                // Do math to find three adjacent cells on the left
                for (int i=-8;i<=8;i+=8) {
                    newCell = cell_tvs.get(currInt + i - 1);
                    addBombCount(newCell);
                }
            }
            // If bomb is on bottom edge (no corners)
            else if (currInt > 72 & currInt < 79) {
                TextView newCell = cell_tvs.get(currInt - 1);
                addBombCount(newCell);
                newCell = cell_tvs.get(currInt + 1);
                addBombCount(newCell);
                // Do math to find three adjacent cells above
                for (int i=7;i<10;i++) {
                    newCell = cell_tvs.get(currInt - i);
                    addBombCount(newCell);
                }
            }
            // If bomb is anywhere in the "middle" (within the edges)
            else {
                TextView newCell;
                for (int i=7;i<10;i++) {
                    newCell = cell_tvs.get(currInt - i);
                    addBombCount(newCell);
                }
                newCell = cell_tvs.get(currInt - 1);
                addBombCount(newCell);
                newCell = cell_tvs.get(currInt + 1);
                addBombCount(newCell);
                for (int i=7;i<10;i++) {
                    newCell = cell_tvs.get(currInt + i);
                    addBombCount(newCell);
                }
            }

        } //end for loop random mines

    }

    private void addBombCount(TextView cell) {
        if (!cell.getHint().toString().equals(getResources().getString(R.string.mine))) {
            cell.setText(String.valueOf(Integer.parseInt(cell.getText().toString()) + 1));
        }
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }

        return -1;
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        // Check if user won
//        for (TextView check : cell_tvs) {
//            if (!check.getHint().toString().equals("dug") && !check.getHint().toString().equals(getResources().getString(R.string.mine))) {
//                break;
//            }
//            Intent intent = new Intent(this,EndScreen.class);
//            intent.putExtra("TimeUsed",String.valueOf(seconds));
//            intent.putExtra("GameState","Win");
//            startActivity(intent);
//        }
        if (gameLose) {
            Intent intent = new Intent(this,EndScreen.class);
            intent.putExtra("TimeUsed",String.valueOf(seconds));
            intent.putExtra("GameState","Lose");
            startActivity(intent);
        }
        // If we are flagging!!!!
        if (digMode == false) {
            // If there is not already a flag and the thing hasn't been dug yet
            if ((!tv.getText().toString().equals(getResources().getString(R.string.flag))) & !tv.getHint().toString().equals("dug")) {
                if (tv.getHint().toString().equals(getResources().getString(R.string.mine))) {
                    tv.setText(R.string.flag);
                    tv.setTextColor(Color.GRAY);
                    tv.setBackgroundColor(Color.LTGRAY);
                    flagCount--;
                    numFlags.setText(String.valueOf(flagCount));
                }
                else {
                    tv.setHint(tv.getText().toString());
                    tv.setText(R.string.flag);
                    tv.setTextColor(Color.GRAY);
                    tv.setBackgroundColor(Color.LTGRAY);
                    flagCount--;
                    numFlags.setText(String.valueOf(flagCount));
                }

                running = true;
            }
            // If the text of the square is a flag
            else if (tv.getText().toString().equals(getResources().getString(R.string.flag))) {
                if (tv.getHint().toString().equals(getResources().getString(R.string.mine))) {
                    tv.setText("-1");
                    tv.setBackgroundColor(Color.GRAY);
                    flagCount++;
                    numFlags.setText(String.valueOf(flagCount));
                }
                else {
                    tv.setText(tv.getHint().toString());
                    tv.setBackgroundColor(Color.GRAY);
                    flagCount++;
                    numFlags.setText(String.valueOf(flagCount));
                }
            }
        }
        else { // digMode == true
            count = 0;
            if (tv.getHint().toString().equals(getResources().getString(R.string.mine))) {
                tv.setText(getResources().getString(R.string.mine));
                for (TextView curr : cell_tvs) {
                    if (curr.getHint().toString().equals(getResources().getString(R.string.mine))) {
                        curr.setText(getResources().getString(R.string.mine));
                    }
                }
                running = false;
                gameLose = true;
            }
            else if (tv.getText().toString().equals("0")) {
                tv.setTextColor(Color.LTGRAY);
                tv.setBackgroundColor(Color.LTGRAY);
                tv.setHint("dug");
                revealAdjacentCells(tv);
                running = true;
            }
            else {
                tv.setBackgroundColor(Color.LTGRAY);
                tv.setTextColor(Color.GRAY);
                tv.setHint("dug");

                running = true;
            }
            for (TextView curr : cell_tvs) {
                if (curr.getHint().toString().equals("dug") || curr.getHint().toString().equals(getResources().getString(R.string.mine))) {
                    count++;
                }
            }
            if (count >=80) {
                Intent intent = new Intent(this, EndScreen.class);
                intent.putExtra("TimeUsed", String.valueOf(seconds));
                intent.putExtra("GameState", "Win");
                startActivity(intent);
            }
        }
    }

    private void revealAdjacentCells(TextView cell) {
        int currInt= findIndexOfCellTextView(cell);
        ArrayList<TextView> adjCells;
        adjCells = new ArrayList<TextView>();

        if (currInt == 0) {
            TextView newCell = cell_tvs.get(currInt + 1);
            adjCells.add(newCell);
            newCell = cell_tvs.get(currInt + 8);
            adjCells.add(newCell);
            newCell = cell_tvs.get(currInt + 9);
            adjCells.add(newCell);
        }
        // If bomb is on top right
        else if (currInt == 7) {
            TextView newCell = cell_tvs.get(currInt - 1);
            adjCells.add(newCell);
            newCell = cell_tvs.get(currInt + 7);
            adjCells.add(newCell);
            newCell = cell_tvs.get(currInt + 8);
            adjCells.add(newCell);
        }
        // If bomb is on bottom left
        else if (currInt == 72) {
            TextView newCell = cell_tvs.get(currInt - 8);
            adjCells.add(newCell);
            newCell = cell_tvs.get(currInt - 7);
            adjCells.add(newCell);
            newCell = cell_tvs.get(currInt + 1);
            adjCells.add(newCell);
        }
        // If bomb is on bottom right
        else if (currInt == 79) {
            TextView newCell = cell_tvs.get(currInt - 9);
            adjCells.add(newCell);
            newCell = cell_tvs.get(currInt - 8);
            adjCells.add(newCell);
            newCell = cell_tvs.get(currInt - 1);
            adjCells.add(newCell);
        }
        // If bomb is on top edge (no corners)
        else if (currInt > 0 & currInt < 7) {
            TextView newCell = cell_tvs.get(currInt - 1);
            adjCells.add(newCell);
            newCell = cell_tvs.get(currInt + 1);
            adjCells.add(newCell);
            // Do math to find three adjacent cells below
            for (int i=7;i<10;i++) {
                newCell = cell_tvs.get(currInt + i);
                adjCells.add(newCell);
            }
        }
        // If bomb is on left edge (no corners)
        else if (currInt % 8 == 0) {
            TextView newCell = cell_tvs.get(currInt - 8);
            adjCells.add(newCell);
            newCell = cell_tvs.get(currInt + 8);
            adjCells.add(newCell);
            // Do math to find three adjacent cells on the right
            for (int i=-8;i<=8;i+=8) {
                newCell = cell_tvs.get(currInt + i + 1);
                adjCells.add(newCell);
            }
        }
        //If bomb is on right edge (no corners)
        else if (currInt % 8 == 7) {
            TextView newCell = cell_tvs.get(currInt - 8);
            adjCells.add(newCell);
            newCell = cell_tvs.get(currInt + 8);
            adjCells.add(newCell);
            // Do math to find three adjacent cells on the left
            for (int i=-8;i<=8;i+=8) {
                newCell = cell_tvs.get(currInt + i - 1);
                adjCells.add(newCell);
            }
        }
        // If bomb is on bottom edge (no corners)
        else if (currInt > 72 & currInt < 79) {
            TextView newCell = cell_tvs.get(currInt - 1);
            adjCells.add(newCell);
            newCell = cell_tvs.get(currInt + 1);
            adjCells.add(newCell);
            // Do math to find three adjacent cells above
            for (int i=7;i<10;i++) {
                newCell = cell_tvs.get(currInt - i);
                adjCells.add(newCell);
            }
        }
        // If bomb is anywhere in the "middle" (within the edges)
        else {
            TextView newCell;
            for (int i=7;i<10;i++) {
                newCell = cell_tvs.get(currInt - i);
                adjCells.add(newCell);
            }
            newCell = cell_tvs.get(currInt - 1);
            adjCells.add(newCell);
            newCell = cell_tvs.get(currInt + 1);
            adjCells.add(newCell);
            for (int i=7;i<10;i++) {
                newCell = cell_tvs.get(currInt + i);
                adjCells.add(newCell);
            }
        }
        for (TextView curr : adjCells) {
            if (curr.getText().toString().equals("0") & !curr.getHint().equals("dug")) {
                curr.setBackgroundColor(Color.LTGRAY);
                curr.setTextColor(Color.LTGRAY);
                curr.setHint("dug");

                revealAdjacentCells(curr);
            }
            else if (!curr.getText().toString().equals("0") & !curr.getText().toString().equals("-1") & !curr.getHint().equals("dug")) {
                curr.setBackgroundColor(Color.LTGRAY);
                curr.setTextColor(Color.GRAY);
                curr.setHint("dug");
            }
        }
    }

    private void runTimer () {
        final TextView timeView = (TextView) findViewById(R.id.NumberTime);
        final Handler handler = new Handler();
        handler.post (new Runnable() {
            @Override
            public void run () {

                String time = String.format("%d",seconds);
                timeView.setText(time);
                if ((running) & (seconds < 99)) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }

        });
    }
}