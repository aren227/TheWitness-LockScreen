package com.aren.thewitnesspuzzle.activity;

import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.animation.PuzzleFadeInAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.PuzzleFadeOutAnimation;
import com.aren.thewitnesspuzzle.puzzle.base.PuzzleBase;
import com.aren.thewitnesspuzzle.puzzle.factory.CustomFixedPuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;
import com.aren.thewitnesspuzzle.puzzle.sound.Sounds;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class PlayActivity extends AppCompatActivity {

    private Game game;
    private PuzzleFactoryManager puzzleFactoryManager;
    private PuzzleFactory currentPuzzleFactory;

    private RelativeLayout root;
    private ImageView nextImage;
    private ImageView skipImage;
    private ImageView retryImage;
    private TextView warningText;
    private ImageView favImage;

    private long seed;
    private UUID factoryUuid;

    private int sequenceIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        root = findViewById(R.id.play_root);
        nextImage = findViewById(R.id.next_puzzle);
        skipImage = findViewById(R.id.skip_puzzle);
        retryImage = findViewById(R.id.retry_puzzle);
        warningText = findViewById(R.id.no_puzzle_warn);
        favImage = findViewById(R.id.favorite);

        game = new Game(this, Game.Mode.PLAY);
        game.setOnSolved(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getPlayProfile();
                        if (profile.getType() == PuzzleFactoryManager.ProfileType.SEQUENCE) {
                            nextPuzzle();
                        } else if (profile.getType() == PuzzleFactoryManager.ProfileType.DEFAULT) {
                            nextImage.setVisibility(View.VISIBLE);
                            skipImage.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        nextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sequenceIndex = 0;
                nextPuzzle();
            }
        });

        skipImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPuzzle();
            }
        });

        retryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sequenceIndex = 0;
                nextPuzzle();
            }
        });

        favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPuzzleFactory instanceof CustomFixedPuzzleFactory) return;

                CustomFixedPuzzleFactory factory = new CustomFixedPuzzleFactory(PlayActivity.this, game.getPuzzle().getUuid());
                if(game.getPuzzle().isFavorite()) {
                    puzzleFactoryManager.remove(factory);
                } else {
                    try {
                        factory.setLiked(game.getPuzzle().getPuzzleBase());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                game.getPuzzle().setFavorite(!game.getPuzzle().isFavorite());
                favImage.setImageResource(game.getPuzzle().isFavorite() ? R.drawable.ic_baseline_favorite : R.drawable.ic_baseline_favorite_border);
            }
        });

        puzzleFactoryManager = new PuzzleFactoryManager(this);

        if (savedInstanceState == null) {
            seed = new Random().nextLong();
            factoryUuid = null;
        } else {
            seed = savedInstanceState.getLong("seed");
            if (puzzleFactoryManager.getLastViewedProfile().getType() == PuzzleFactoryManager.ProfileType.DEFAULT) {
                factoryUuid = UUID.fromString(savedInstanceState.getString("uuid"));
            }
            sequenceIndex = savedInstanceState.getInt("sequenceIndex");
        }

        if (generatePuzzle()) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            root.addView(game.getSurfaceView(), params);

            nextImage.setVisibility(View.GONE);
            nextImage.bringToFront();

            skipImage.setVisibility(View.VISIBLE);
            skipImage.bringToFront();

            retryImage.setVisibility(View.GONE);
            retryImage.bringToFront();

            if (currentPuzzleFactory instanceof CustomFixedPuzzleFactory) {
                favImage.setVisibility(View.GONE);
            } else {
                favImage.setVisibility(View.VISIBLE);
            }
            favImage.bringToFront();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("seed", seed);
        if (puzzleFactoryManager.getLastViewedProfile().getType() == PuzzleFactoryManager.ProfileType.DEFAULT) {
            savedInstanceState.putString("uuid", factoryUuid.toString());
        }
        savedInstanceState.putInt("sequenceIndex", sequenceIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        game.update();
    }

    @Override
    public void onStop() {
        super.onStop();
        game.close();
    }

    public void nextPuzzle() {
        seed = new Random(seed).nextLong();
        factoryUuid = null;

        PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getPlayProfile();
        if (profile.getType() == PuzzleFactoryManager.ProfileType.SEQUENCE) {
            if (sequenceIndex >= profile.getSequence().size()) {
                // SUCCESS!
                nextImage.setVisibility(View.VISIBLE);
                skipImage.setVisibility(View.GONE);
                retryImage.setVisibility(View.GONE);

                game.stopExternalSound();
                game.getPuzzle().setUntouchable(true);
                game.stopTimerMode();

                return;
            }
        }

        if (!generatePuzzle()) {
            root.removeView(game.getSurfaceView());
        }
        nextImage.setVisibility(View.GONE);
        skipImage.setVisibility(View.VISIBLE);
        retryImage.setVisibility(View.GONE);

        favImage.setImageResource(R.drawable.ic_baseline_favorite_border);
        if (currentPuzzleFactory instanceof CustomFixedPuzzleFactory) {
            favImage.setVisibility(View.GONE);
        } else {
            favImage.setVisibility(View.VISIBLE);
        }
    }

    public boolean generatePuzzle() {
        PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getPlayProfile();
        if (profile.getType() == PuzzleFactoryManager.ProfileType.DEFAULT) {
            currentPuzzleFactory = null;
            if (factoryUuid == null) {
                currentPuzzleFactory = puzzleFactoryManager.getPlayProfile().getRandomPuzzleFactory(new Random());
            } else {
                for (PuzzleFactory f : puzzleFactoryManager.getPlayProfile().getActivatedPuzzleFactories()) {
                    if (f.getUuid().equals(factoryUuid)) {
                        currentPuzzleFactory = f;
                        break;
                    }
                }
            }
            if (currentPuzzleFactory == null) return false;
            factoryUuid = currentPuzzleFactory.getUuid();
            PuzzleRenderer puzzleRenderer = currentPuzzleFactory.generate(game, new Random(seed));
            game.setPuzzle(puzzleRenderer);
            game.update();
            return true;
        } else if (profile.getType() == PuzzleFactoryManager.ProfileType.SEQUENCE) {
            if (profile.getSequence().size() == 0) return false;

            if (sequenceIndex >= profile.getSequence().size()) {
                sequenceIndex = 0;
            }

            if (sequenceIndex == 0) {
                game.playSound(Sounds.CHALLENGE_START);
                if (profile.getMusicFile().exists()) {
                    game.playExternalSound(profile.getMusicFile().getPath());
                }
                game.setTimerMode(profile.getTimeLength(), new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nextImage.setVisibility(View.GONE);
                                skipImage.setVisibility(View.GONE);
                                retryImage.setVisibility(View.VISIBLE);

                                game.getPuzzle().addAnimation(new PuzzleFadeOutAnimation(game.getPuzzle(), 1000));
                                game.playSound(Sounds.ABORT_TRACING);

                                game.update();
                            }
                        });
                    }
                });
            }

            PuzzleRenderer puzzleRenderer = profile.getSequence().get(sequenceIndex).generate(game, new Random(seed));
            game.setPuzzle(puzzleRenderer);

            if (sequenceIndex == 0) {
                puzzleRenderer.addAnimation(new PuzzleFadeInAnimation(puzzleRenderer, 2000));
            }

            game.update();

            sequenceIndex++;

            return true;
        }
        return false;
    }
}
