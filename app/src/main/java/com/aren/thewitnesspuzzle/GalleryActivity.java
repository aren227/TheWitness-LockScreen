package com.aren.thewitnesspuzzle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import java.util.List;
import java.util.Random;

public class GalleryActivity extends AppCompatActivity {

    private PuzzleFactoryManager puzzleFactoryManager;

    private RelativeLayout root;
    private GalleryAdapter adapter;

    private Game tempGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        puzzleFactoryManager = new PuzzleFactoryManager(this);

        adapter = new GalleryAdapter(this, puzzleFactoryManager);

        RecyclerView recyclerView = findViewById(R.id.gallery_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);

        root = findViewById(R.id.gallery_root);

        startRenderWorker();
    }

    private void startRenderWorker(){
        tempGame = new Game(this);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(512, 512);
        params.addRule(RelativeLayout.ALIGN_PARENT_START, 1);
        // Invisible but still can be rendered
        params.setMargins(-512, -512, 0, 0);

        root.addView(tempGame.getSurfaceView(), params);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PuzzleFactory> factories = puzzleFactoryManager.getAllPuzzleFactories();
                for(PuzzleFactory factory : factories){
                    Puzzle puzzle = factory.generate(tempGame, new Random());

                    tempGame.setPuzzle(puzzle);

                    synchronized (tempGame.getSurfaceView()){
                        tempGame.getSurfaceView().capture();
                        try {
                            tempGame.getSurfaceView().wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.addPreview(new GalleryPreview(factory, tempGame.getSurfaceView().bitmap, factory.getName()));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }
}
