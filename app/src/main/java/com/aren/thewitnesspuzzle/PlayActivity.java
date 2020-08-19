package com.aren.thewitnesspuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import java.util.List;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    private Game game;
    private PuzzleFactoryManager puzzleFactoryManager;

    private RelativeLayout root;
    private ImageView nextImage;
    private TextView warningText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        root = findViewById(R.id.play_root);
        nextImage = findViewById(R.id.next_puzzle);
        warningText = findViewById(R.id.no_puzzle_warn);

        game = new Game(this);
        game.setOnSolved(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nextImage.setVisibility(View.VISIBLE);
                        nextImage.bringToFront();
                    }
                });
            }
        });

        nextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!setRandomPuzzle()){
                    root.removeView(game.getSurfaceView());
                }
                nextImage.setVisibility(View.GONE);
            }
        });

        puzzleFactoryManager = new PuzzleFactoryManager(this);

        if(setRandomPuzzle()){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            root.addView(game.getSurfaceView(), params);
        }
    }

    public boolean setRandomPuzzle(){
        Random random = new Random();
        List<PuzzleFactory> factories = puzzleFactoryManager.getActivatedPuzzleFactories();
        if(factories.size() == 0) return false;
        Puzzle puzzle = factories.get(random.nextInt(factories.size())).generate(game, random);
        game.setPuzzle(puzzle);
        game.update();
        return true;
    }
}