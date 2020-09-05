package com.aren.thewitnesspuzzle.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import java.util.Random;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class PlayActivity extends AppCompatActivity {

    private Game game;
    private PuzzleFactoryManager puzzleFactoryManager;

    private RelativeLayout root;
    private ImageView nextImage;
    private ImageView skipImage;
    private TextView warningText;

    private long seed;
    private UUID factoryUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        root = findViewById(R.id.play_root);
        nextImage = findViewById(R.id.next_puzzle);
        skipImage = findViewById(R.id.skip_puzzle);
        warningText = findViewById(R.id.no_puzzle_warn);

        game = new Game(this, Game.Mode.PLAY);
        game.setOnSolved(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nextImage.setVisibility(View.VISIBLE);
                        skipImage.setVisibility(View.GONE);
                    }
                });
            }
        });

        nextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPuzzle();
            }
        });

        skipImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPuzzle();
            }
        });

        puzzleFactoryManager = new PuzzleFactoryManager(this);

        if (savedInstanceState == null) {
            seed = new Random().nextLong();
            factoryUuid = null;
        } else {
            seed = savedInstanceState.getLong("seed");
            factoryUuid = UUID.fromString(savedInstanceState.getString("uuid"));
        }

        if (generatePuzzle()) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            root.addView(game.getSurfaceView(), params);

            nextImage.setVisibility(View.GONE);
            nextImage.bringToFront();

            skipImage.setVisibility(View.VISIBLE);
            skipImage.bringToFront();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("seed", seed);
        savedInstanceState.putString("uuid", factoryUuid.toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    public void nextPuzzle() {
        seed = new Random(seed).nextLong();
        factoryUuid = null;
        if (!generatePuzzle()) {
            root.removeView(game.getSurfaceView());
        }
        nextImage.setVisibility(View.GONE);
        skipImage.setVisibility(View.VISIBLE);
    }

    public boolean generatePuzzle() {
        PuzzleFactory factory = null;
        if (factoryUuid == null) {
            factory = puzzleFactoryManager.getPlayProfile().getRandomPuzzleFactory(new Random());
        } else {
            for (PuzzleFactory f : puzzleFactoryManager.getPlayProfile().getActivatedPuzzleFactories()) {
                if (f.getUuid().equals(factoryUuid)) {
                    factory = f;
                    break;
                }
            }
        }
        if (factory == null) return false;
        factoryUuid = factory.getUuid();
        Puzzle puzzle = factory.generate(game, new Random(seed));
        game.setPuzzle(puzzle);
        game.update();
        return true;
        /*List<PuzzleFactory> factories = puzzleFactoryManager.getPlayProfile().getActivatedPuzzleFactories();
        if(factories.size() == 0) return false;
        Puzzle puzzle = factories.get(random.nextInt(factories.size())).generate(game, random);
        game.setPuzzle(puzzle);
        game.update();
        return true;*/
    }
}
