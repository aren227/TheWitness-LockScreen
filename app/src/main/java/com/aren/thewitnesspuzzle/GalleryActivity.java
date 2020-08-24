package com.aren.thewitnesspuzzle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GalleryActivity extends AppCompatActivity {

    private PuzzleFactoryManager puzzleFactoryManager;

    private RelativeLayout root;
    private GalleryAdapter adapter;

    private TextView status;

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

        status = findViewById(R.id.gallery_text);
        puzzleFactoryManager.setOnUpdate(new Runnable() {
            @Override
            public void run() {
                updateStatusText();
            }
        });
        updateStatusText();

        startRenderWorker();
    }

    public void updateStatusText(){
        status.setText(String.format(getString(R.string.gallery_status), puzzleFactoryManager.getAllPuzzleFactories().size(), puzzleFactoryManager.getActivatedPuzzleFactories().size()));
    }

    private void startRenderWorker(){
        tempGame = new Game(this);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(512, 512);
        params.addRule(RelativeLayout.ALIGN_PARENT_START, 1);
        // Invisible but still can be rendered
        params.setMargins(-512, -512, 0, 0);

        root.addView(tempGame.getSurfaceView(), params);
        tempGame.getSurfaceView().getHolder().setFixedSize(512, 512);

        Bitmap notLoaded = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(notLoaded);
        canvas.drawColor(Color.GRAY);
        canvas.drawBitmap(notLoaded, 0, 0, null);

        // Lazy Loading
        final List<GalleryPreview> previews = new ArrayList<>();
        for(PuzzleFactory factory : puzzleFactoryManager.getAllPuzzleFactories()){
            GalleryPreview preview = new GalleryPreview(factory, notLoaded, factory.getName());
            previews.add(preview);
            adapter.addPreview(preview);
        }
        adapter.notifyDataSetChanged();

        // Generate and Query puzzles
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(PuzzleFactory factory : puzzleFactoryManager.getAllPuzzleFactories()){
                    Puzzle puzzle = factory.generate(tempGame, new Random());
                    tempGame.getSurfaceView().glRenderer.addRenderQueue(puzzle);
                }
            }
        }).start();

        // Receive rendered results
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(GalleryPreview preview : previews){
                    while(true){
                        tempGame.getSurfaceView().requestRender();
                        synchronized (tempGame.getSurfaceView().glRenderer){
                            try {
                                tempGame.getSurfaceView().glRenderer.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(tempGame.getSurfaceView().glRenderer.renderResults.size() > 0) break;
                    }
                    preview.bitmap = tempGame.getSurfaceView().glRenderer.renderResults.poll();

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
