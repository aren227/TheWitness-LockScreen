package com.aren.thewitnesspuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.HexagonPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;

import java.util.ArrayList;
import java.util.List;

public class CreatePatternActivity extends PuzzleEditorActivity {

    TextView instructionTextView;
    ImageView deleteImageView;
    ImageView nextImageView;
    boolean viewInit = false;

    enum State {INIT, FIRST_DRAWN, VALIDATE, DONE}
    State state = State.INIT;
    List<Integer> pattern = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_create_pattern, root, true);

        instructionTextView = view.findViewById(R.id.instruction);
        deleteImageView = view.findViewById(R.id.delete);
        nextImageView = view.findViewById(R.id.next);
        viewInit = true;

        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePattern();
            }
        });

        nextImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state == State.FIRST_DRAWN){
                    puzzle.setCustomPattern(pattern);
                    puzzle.setCursor(null);
                    game.update();

                    state = State.VALIDATE;
                    updateUI();
                }
                else if(state == State.DONE){
                    // Save and exit
                }
            }
        });

        state = State.INIT;

        game.setOnSolved(new Runnable() {
            @Override
            public void run() {
                if(puzzle.getCustomPattern() != null){
                    state = State.DONE;
                }
                else{
                    pattern = puzzle.getCursor().getVisitedVertexIndices();
                    state = State.FIRST_DRAWN;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        });
        game.setOnPreTouched(new Runnable() {
            @Override
            public void run() {
                if(state == State.FIRST_DRAWN){
                    state = State.INIT;
                }
                else if(state == State.DONE){
                    state = State.VALIDATE;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        });
        updateUI();
    }

    protected void updateUI(){
        if(!viewInit) return;
        if(state == State.INIT){
            instructionTextView.setText(R.string.create_pattern_draw);
            deleteImageView.setVisibility(View.INVISIBLE);
            nextImageView.setVisibility(View.INVISIBLE);
        }
        else if(state == State.FIRST_DRAWN){
            deleteImageView.setVisibility(View.INVISIBLE);
            nextImageView.setImageResource(R.drawable.ic_navigate_next_black_24dp);
            nextImageView.setVisibility(View.VISIBLE);
        }
        else if(state == State.VALIDATE){
            instructionTextView.setText(R.string.create_pattern_validate);
            deleteImageView.setVisibility(View.VISIBLE);
            nextImageView.setVisibility(View.INVISIBLE);
        }
        else{
            nextImageView.setImageResource(R.drawable.ic_baseline_check_24);
            deleteImageView.setVisibility(View.VISIBLE);
            nextImageView.setVisibility(View.VISIBLE);
        }
    }

    protected void deletePattern(){
        puzzle.setCustomPattern(null);
        puzzle.setCursor(null);
        game.update();

        state = State.INIT;
        updateUI();
    }

    @Override
    protected void resetPuzzle(){
        super.resetPuzzle();
        deletePattern();
    }


}