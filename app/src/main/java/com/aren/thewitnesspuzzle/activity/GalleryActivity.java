package com.aren.thewitnesspuzzle.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.gallery.GalleryAdapter;
import com.aren.thewitnesspuzzle.gallery.GalleryPreview;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.ErrorPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.factory.CustomPatternPuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryActivity extends AppCompatActivity {

    private PuzzleFactoryManager puzzleFactoryManager;

    private RelativeLayout root;
    private GalleryAdapter adapter;

    private EditText profileNameEditText;
    private ImageView profileDropdownImageView;
    private ImageView profileLockImageView;
    private ImageView profilePlayImageView;
    private TextView profileCountTextView;
    private ImageView profileAddImageView;
    private ImageView profileDeleteImageView;

    private Game tempGame;
    private Thread puzzleGenerationThread, puzzleRenderThread;

    private List<PuzzleFactoryManager.Profile> profiles;

    private View dropdownView;
    private View dropdownBgView;

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

        profileNameEditText = findViewById(R.id.profile_name);
        profileDropdownImageView = findViewById(R.id.dropdown);
        profileLockImageView = findViewById(R.id.profile_lock);
        profilePlayImageView = findViewById(R.id.profile_play);
        profileCountTextView = findViewById(R.id.profile_count);
        profileAddImageView = findViewById(R.id.profile_add);
        profileDeleteImageView = findViewById(R.id.profile_delete);

        profiles = puzzleFactoryManager.getProfiles();

        profileNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                puzzleFactoryManager.getLastViewedProfile().setName(s.toString());
            }
        });

        profileDropdownImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dropdownView == null) {
                    // Manually implement spinner

                    dropdownBgView = new View(GalleryActivity.this);
                    dropdownBgView.setBackgroundColor(0x7f000000);
                    RelativeLayout.LayoutParams bgParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    bgParams.setMargins(0, findViewById(R.id.head).getHeight(), 0, 0);
                    dropdownBgView.setLayoutParams(bgParams);
                    dropdownBgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeSpinner();
                        }
                    });
                    root.addView(dropdownBgView);

                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    dropdownView = inflater.inflate(R.layout.gallery_dropdown_list, root, false);

                    int clipedCount = Math.min(profiles.size(), 5);
                    int totalHeight = 2 * (clipedCount - 1) + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()) * clipedCount;
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(profileNameEditText.getWidth(), totalHeight);
                    params.setMargins(profileNameEditText.getLeft(), profileNameEditText.getTop() + profileNameEditText.getHeight(), 0, 0);
                    dropdownView.setLayoutParams(params);
                    root.addView(dropdownView);

                    LinearLayout linearLayout = findViewById(R.id.list);
                    for (int i = 0; i < profiles.size(); i++) {
                        if (i > 0) {
                            View horizon = new View(GalleryActivity.this);
                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
                            params1.setMargins(16, 0, 16, 0);
                            horizon.setLayoutParams(params1);
                            horizon.setBackgroundColor(0xff7f7f7f);
                            linearLayout.addView(horizon);
                        }

                        View itemView = inflater.inflate(R.layout.gallery_dropdown_item, linearLayout, false);
                        LinearLayout leftStatus = itemView.findViewById(R.id.profile_status);
                        ImageView leftStatusLock = itemView.findViewById(R.id.profile_status_lock);
                        ImageView leftStatusPlay = itemView.findViewById(R.id.profile_status_play);
                        TextView profileName = itemView.findViewById(R.id.profile_item_name);

                        if (puzzleFactoryManager.getLockProfile().equals(profiles.get(i))) {
                            leftStatus.setVisibility(View.VISIBLE);
                            leftStatusLock.setVisibility(View.VISIBLE);
                        }
                        if (puzzleFactoryManager.getPlayProfile().equals(profiles.get(i))) {
                            leftStatus.setVisibility(View.VISIBLE);
                            leftStatusPlay.setVisibility(View.VISIBLE);
                        }
                        profileName.setText(profiles.get(i).getName());

                        final PuzzleFactoryManager.Profile profile = profiles.get(i);
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!puzzleFactoryManager.isRemovedProfile(profile)) {
                                    profile.markAsLastViewed();
                                    removeSpinner();
                                    updateGallery();
                                }
                            }
                        });
                        linearLayout.addView(itemView);
                    }
                } else {
                    removeSpinner();
                }
            }
        });

        profileLockImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GalleryActivity.this, "This profile will be used in the lock screen.", Toast.LENGTH_SHORT).show();
                puzzleFactoryManager.getLastViewedProfile().assignToLock();
                removeSpinner();
                updateGallery();
            }
        });

        profilePlayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GalleryActivity.this, "This profile will be used in the play mode.", Toast.LENGTH_SHORT).show();
                puzzleFactoryManager.getLastViewedProfile().assignToPlay();
                removeSpinner();
                updateGallery();
            }
        });

        profileAddImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                puzzleFactoryManager.createProfile("New Profile").markAsLastViewed();
                removeSpinner();
                updateGallery();
            }
        });

        profileDeleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (puzzleFactoryManager.getLastViewedProfile().equals(puzzleFactoryManager.getDefaultProfile())) {
                    Toast.makeText(GalleryActivity.this, "You can't delete the default profile.", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
                builder.setTitle("Remove Profile");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                puzzleFactoryManager.removeProfile(puzzleFactoryManager.getLastViewedProfile());
                                removeSpinner();
                                updateGallery();
                            }
                        });
                builder.setNegativeButton("No", null);
                AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(0xff000000);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(0xff000000);

                removeSpinner();
            }
        });

        puzzleFactoryManager.setOnUpdate(new Runnable() {
            @Override
            public void run() {
                updateStatusText();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateGallery();
    }

    public void removeSpinner() {
        if (dropdownView != null) {
            root.removeView(dropdownView);
            root.removeView(dropdownBgView);
            dropdownView = dropdownBgView = null;
        }
    }

    public void updateGallery() {
        profiles = puzzleFactoryManager.getProfiles();

        PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getLastViewedProfile();

        profileNameEditText.setText(profile.getName());

        profileLockImageView.setAlpha(puzzleFactoryManager.getLockProfile().equals(profile) ? 1 : 0.3f);
        profilePlayImageView.setAlpha(puzzleFactoryManager.getPlayProfile().equals(profile) ? 1 : 0.3f);

        updateStatusText();
        startRenderWorker();
    }

    public void updateStatusText() {
        PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getLastViewedProfile();
        profileCountTextView.setText(String.format("%d/%d", profile.getActivatedPuzzleFactories().size(), puzzleFactoryManager.getAllPuzzleFactories().size()));
    }

    private void startRenderWorker() {
        if (tempGame != null && tempGame.getSurfaceView().getParent() != null) {
            root.removeView(tempGame.getSurfaceView());
        }
        if (puzzleGenerationThread != null) {
            puzzleGenerationThread.interrupt();
        }
        if (puzzleRenderThread != null) {
            puzzleRenderThread.interrupt();
        }

        tempGame = new Game(this, Game.Mode.GALLERY);

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

        adapter.clearPreviews();

        // Lazy Loading
        final List<GalleryPreview> previewsToRender = new ArrayList<>();
        for (PuzzleFactory factory : puzzleFactoryManager.getAllPuzzleFactories()) {
            // Check config error
            /*long start = System.currentTimeMillis();
            if(factory instanceof CustomPatternPuzzleFactory && factory.generate(tempGame, new Random()) == null){
                continue;
            }
            if(factory instanceof CustomRandomPuzzleFactory && factory.generate(tempGame, new Random()) == null){
                continue;
            }*/

            GalleryPreview preview = new GalleryPreview(factory, notLoaded, factory.getName());
            if (factory.getThumbnailCache() != null) {
                preview.bitmap = factory.getThumbnailCache();
            } else {
                previewsToRender.add(preview);
            }
            adapter.addPreview(preview);
        }

        // A button for adding new puzzle
        adapter.addPreview(GalleryPreview.addButton());

        adapter.notifyDataSetChanged();

        // Generate and Query puzzles
        puzzleGenerationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (GalleryPreview preview : previewsToRender) {
                    Puzzle puzzle;
                    if (preview.puzzleFactory instanceof CustomPatternPuzzleFactory)
                        puzzle = ((CustomPatternPuzzleFactory) preview.puzzleFactory).generateWithPattern(tempGame, new Random(), true);
                    else puzzle = preview.puzzleFactory.generate(tempGame, new Random());

                    // Load Failed
                    if (puzzle == null) puzzle = new ErrorPuzzle(tempGame);

                    tempGame.getSurfaceView().glRenderer.addRenderQueue(puzzle);
                }
            }
        });
        puzzleGenerationThread.start();

        // Receive rendered results
        puzzleRenderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                tempGame.getSurfaceView().glRenderer.setGalleryRenderMode();
                for (GalleryPreview preview : previewsToRender) {
                    try {
                        while (true) {
                            tempGame.getSurfaceView().requestRender();
                            synchronized (tempGame.getSurfaceView().glRenderer) {
                                tempGame.getSurfaceView().glRenderer.wait();
                            }
                            if (tempGame.getSurfaceView().glRenderer.getRenderedResults().size() > 0)
                                break;
                        }

                        preview.bitmap = tempGame.getSurfaceView().glRenderer.getRenderedResults().poll();
                        preview.puzzleFactory.setThumbnailCache(preview.bitmap);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        root.removeView(tempGame.getSurfaceView());
                    }
                });
            }
        });
        puzzleRenderThread.start();
    }
}
