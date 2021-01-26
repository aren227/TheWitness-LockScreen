package com.aren.thewitnesspuzzle.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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
import com.aren.thewitnesspuzzle.core.puzzle.ErrorPuzzle;
import com.aren.thewitnesspuzzle.dialog.NewProfileDialog;
import com.aren.thewitnesspuzzle.gallery.GalleryAdapter;
import com.aren.thewitnesspuzzle.gallery.GalleryFolderPreview;
import com.aren.thewitnesspuzzle.gallery.GalleryPuzzlePreview;
import com.aren.thewitnesspuzzle.gallery.ItemMoveCallback;
import com.aren.thewitnesspuzzle.gallery.OnPreviewClick;
import com.aren.thewitnesspuzzle.gallery.OnUpdate;
import com.aren.thewitnesspuzzle.gallery.PuzzleOrderAdapter;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.factory.CustomPatternPuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
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

    private PuzzleOrderAdapter orderAdapter;

    private TextView orderAdapterHintText;
    private LinearLayout sequenceSettingsContainer;

    private TextView musicNameTextView;
    private ImageView attachMusicFileImageView;
    private ImageView playMusicFileImageView;
    private ImageView detachMusicFileImageView;
    private MediaPlayer mediaPlayer;
    private EditText timeLengthMEditText;
    private EditText timeLengthSEditText;

    private static Bitmap notLoadedBitmap;

    private UUID currentFolder = PuzzleFactoryManager.rootFolderUuid;

    private LinearLayout folderNavigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        puzzleFactoryManager = new PuzzleFactoryManager(this);

        OnPreviewClick onPreviewClick = new OnPreviewClick() {
            @Override
            public void onClick(GalleryPuzzlePreview preview) {
                PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getLastViewedProfile();
                if(profile.getType() == PuzzleFactoryManager.ProfileType.DEFAULT){
                    puzzleFactoryManager.getLastViewedProfile().setActivated(preview.puzzleFactory, !puzzleFactoryManager.getLastViewedProfile().isActivated(preview.puzzleFactory));
                    adapter.notifyDataSetChanged();
                }
                else if(profile.getType() == PuzzleFactoryManager.ProfileType.SEQUENCE){
                    orderAdapter.addPuzzle(preview.puzzleFactory);
                    orderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onClick(GalleryFolderPreview preview) {
                currentFolder = preview.folderUuid;
                updateGallery();
            }
        };

        adapter = new GalleryAdapter(this, puzzleFactoryManager, onPreviewClick);

        RecyclerView recyclerView = findViewById(R.id.gallery_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);

        orderAdapterHintText = findViewById(R.id.order_hint);
        sequenceSettingsContainer = findViewById(R.id.sequence_settings);
        sequenceSettingsContainer.setVisibility(View.GONE);

        OnUpdate onSequenceUpdate = new OnUpdate() {
            @Override
            public void onUpdate() {
                if(orderAdapter.getItemCount() > 0){
                    orderAdapterHintText.setVisibility(View.GONE);
                }
                else{
                    orderAdapterHintText.setVisibility(View.VISIBLE);
                }
                PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getLastViewedProfile();
                profile.setSequence(orderAdapter.getSequence());
            }
        };

        orderAdapter = new PuzzleOrderAdapter(this, puzzleFactoryManager, onSequenceUpdate);

        RecyclerView orderRecyclerView = findViewById(R.id.order);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(orderAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(orderRecyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        orderRecyclerView.setLayoutManager(manager);
        orderRecyclerView.setAdapter(orderAdapter);

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
                View.OnClickListener onDefaultClicked = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        puzzleFactoryManager.createProfile("New Profile", PuzzleFactoryManager.ProfileType.DEFAULT).markAsLastViewed();
                        removeSpinner();
                        updateGallery();
                    }
                };
                View.OnClickListener onSequenceClicked = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        puzzleFactoryManager.createProfile("New Challenge", PuzzleFactoryManager.ProfileType.SEQUENCE).markAsLastViewed();
                        removeSpinner();
                        updateGallery();
                    }
                };
                NewProfileDialog dialog = new NewProfileDialog(GalleryActivity.this, onDefaultClicked, onSequenceClicked);
                dialog.show();
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

        musicNameTextView = findViewById(R.id.music_name);
        attachMusicFileImageView = findViewById(R.id.attach_music_file);
        playMusicFileImageView = findViewById(R.id.play_music_file);
        detachMusicFileImageView = findViewById(R.id.detach_music_file);

        attachMusicFileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 12345);
            }
        });
        playMusicFileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null){
                    mediaPlayer.release();
                }
                PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getLastViewedProfile();
                if(profile.hasMusic()){
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(profile.getMusicFile().getPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        detachMusicFileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null){
                    mediaPlayer.release();
                }
                PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getLastViewedProfile();
                profile.removeMusic();
                musicNameTextView.setText("");
            }
        });

        timeLengthMEditText = findViewById(R.id.sequence_time_m);
        timeLengthSEditText = findViewById(R.id.sequence_time_s);

        showSequenceTimeLength();

        timeLengthMEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                timeLengthMEditText.removeTextChangedListener(this);

                try{
                    int i = Integer.parseInt(s.toString());
                    s.replace(0, s.length(), "" + Math.min(Math.max(i, 0), 59));
                }
                catch (Exception e){
                    s.replace(0, s.length(), "0");
                }

                saveSequenceTimeLength();

                timeLengthMEditText.addTextChangedListener(this);
            }
        });

        timeLengthSEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                timeLengthSEditText.removeTextChangedListener(this);

                try{
                    int i = Integer.parseInt(s.toString());
                    s.replace(0, s.length(), "" + Math.min(Math.max(i, 0), 59));
                }
                catch (Exception e){
                    s.replace(0, s.length(), "0");
                }

                saveSequenceTimeLength();

                timeLengthSEditText.addTextChangedListener(this);
            }
        });

        folderNavigator = findViewById(R.id.folder_path);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateGallery();
    }

    public String getNameFromUri(Uri uri){
        try{
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return "No Name";
            }
            cursor.moveToFirst();

            String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));

            cursor.close();

            return fileName;
        }
        catch (Exception e){
            e.printStackTrace();
            return "No Name";
        }
    }

    public void saveSequenceTimeLength(){
        int m = 0, s = 0;
        try{
            m = Integer.parseInt(timeLengthMEditText.getText().toString());
        }
        catch (Exception e){}
        try{
            s = Integer.parseInt(timeLengthSEditText.getText().toString());
        }
        catch (Exception e){}
        puzzleFactoryManager.getLastViewedProfile().setTimeLength(m * 60 + s);
    }

    public void showSequenceTimeLength(){
        PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getLastViewedProfile();
        int m = profile.getTimeLength() / 60;
        int s = profile.getTimeLength() % 60;
        timeLengthMEditText.setText("" + m);
        timeLengthSEditText.setText("" + s);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12345 && resultCode == RESULT_OK) {
            if(data.getData() == null) return;
            boolean pass = true;

            String fileName = getNameFromUri(data.getData());
            if(!fileName.endsWith(".mp3") && !fileName.endsWith(".wav")){
                pass = false;
            }

            MediaPlayer player = null;
            if(pass){
                try{
                    player = MediaPlayer.create(this, data.getData());
                }
                catch (Exception e){
                    pass = false;
                }
            }

            if(pass){
                try {
                    puzzleFactoryManager.getLastViewedProfile().setMusic(getContentResolver().openInputStream(data.getData()), getNameFromUri(data.getData()));
                    musicNameTextView.setText(puzzleFactoryManager.getLastViewedProfile().getMusicName());

                    puzzleFactoryManager.getLastViewedProfile().setTimeLength(player.getDuration() / 1000 + 2);
                    showSequenceTimeLength();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(this, "Please select a valid mp3 or wav file.", Toast.LENGTH_LONG).show();
            }

        }
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

        if(profile.getType() == PuzzleFactoryManager.ProfileType.DEFAULT){
            sequenceSettingsContainer.setVisibility(View.GONE);
        }
        else if(profile.getType() == PuzzleFactoryManager.ProfileType.SEQUENCE){
            sequenceSettingsContainer.setVisibility(View.VISIBLE);

            orderAdapter.setSequence(profile.getSequence());
            orderAdapter.notifyDataSetChanged();

            String musicName = "";
            if(profile.hasMusic()){
                musicName = profile.getMusicName();
            }
            musicNameTextView.setText(musicName);

            if(profile.getSequence().size() == 0){
                orderAdapterHintText.setVisibility(View.VISIBLE);
            }
            else{
                orderAdapterHintText.setVisibility(View.GONE);
            }

            showSequenceTimeLength();
        }

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

        adapter.clearPreviews();

        // Lazy Loading
        final List<GalleryPuzzlePreview> previewsToRender = new ArrayList<>();

        Map<UUID, GalleryFolderPreview> folderPreviewMap = new HashMap<>();
        for (PuzzleFactoryManager.Folder folder : puzzleFactoryManager.getChildFolders(currentFolder)) {
            GalleryFolderPreview preview = new GalleryFolderPreview(folder.getUuid(), new ArrayList<GalleryPuzzlePreview>(), folder.getName());
            folderPreviewMap.put(folder.getUuid(), preview);
            adapter.addPreview(preview);
        }

        for (PuzzleFactory factory : puzzleFactoryManager.getAllPuzzleFactories()) {
            UUID uuid = factory.getConfig().getParentFolderUuid();
            while (uuid != PuzzleFactoryManager.rootFolderUuid && !folderPreviewMap.containsKey(uuid)) {
                PuzzleFactoryManager.Folder folder = puzzleFactoryManager.getFolder(uuid);
                if (folder == null)
                    uuid = PuzzleFactoryManager.rootFolderUuid;
                else
                    uuid = folder.getParentFolderUuid();
            }

            if (folderPreviewMap.containsKey(uuid)) {
                GalleryFolderPreview preview = folderPreviewMap.get(uuid);
                if (preview != null && preview.puzzlePreviews.size() < 4) {
                    preview.puzzlePreviews.add(new GalleryPuzzlePreview(factory, getNotLoadedBitmap(), factory.getName()));
                }
            }
        }

        // Fill folder contents first
        for (Object object : adapter.getItems()) {
            if (object instanceof GalleryFolderPreview) {
                for (GalleryPuzzlePreview preview : ((GalleryFolderPreview) object).puzzlePreviews) {
                    if (preview.puzzleFactory.getThumbnailCache() != null) {
                        preview.bitmap = preview.puzzleFactory.getThumbnailCache();
                    } else {
                        previewsToRender.add(preview);
                    }
                }
            }
        }

        for (PuzzleFactory factory : puzzleFactoryManager.getAllPuzzleFactories()) {
            if (factory.getConfig().getParentFolderUuid().equals(currentFolder)) {
                GalleryPuzzlePreview puzzlePreview = new GalleryPuzzlePreview(factory, getNotLoadedBitmap(), factory.getName());
                if (puzzlePreview.puzzleFactory.getThumbnailCache() != null) {
                    puzzlePreview.bitmap = puzzlePreview.puzzleFactory.getThumbnailCache();
                } else {
                    previewsToRender.add(puzzlePreview);
                }
                adapter.addPreview(puzzlePreview);
            }
        }

        /*for (PuzzleFactory factory : puzzleFactoryManager.getAllPuzzleFactories()) {
            GalleryPuzzlePreview preview = new GalleryPuzzlePreview(factory, getNotLoadedBitmap(), factory.getName());
            if (factory.getThumbnailCache() != null) {
                preview.bitmap = factory.getThumbnailCache();
            } else {
                previewsToRender.add(preview);
            }
            adapter.addPreview(preview);
        }*/

        // A button for adding new puzzle
        adapter.addPreview(GalleryPuzzlePreview.addButton());

        adapter.notifyDataSetChanged();

        // Generate and Query puzzles
        puzzleGenerationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (GalleryPuzzlePreview preview : previewsToRender) {
                    PuzzleRenderer puzzleRenderer = null;
                    try {
                        if (preview.puzzleFactory instanceof CustomPatternPuzzleFactory)
                            puzzleRenderer = ((CustomPatternPuzzleFactory) preview.puzzleFactory).generateWithPattern(tempGame, new Random(), true);
                        else puzzleRenderer = preview.puzzleFactory.generate(tempGame, new Random());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Load Failed
                    if (puzzleRenderer == null) puzzleRenderer = new PuzzleRenderer(tempGame, new ErrorPuzzle());

                    tempGame.getSurfaceView().glRenderer.addRenderQueue(puzzleRenderer);
                }
            }
        });
        puzzleGenerationThread.start();

        // Receive rendered results
        puzzleRenderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                tempGame.getSurfaceView().glRenderer.setGalleryRenderMode();
                for (GalleryPuzzlePreview preview : previewsToRender) {
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

        for (int i = 1; i < folderNavigator.getChildCount(); i++) {
            folderNavigator.removeViewAt(i);
        }

        if (!currentFolder.equals(PuzzleFactoryManager.rootFolderUuid)) {
            folderNavigator.setVisibility(View.VISIBLE);

            folderNavigator.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFolder = PuzzleFactoryManager.rootFolderUuid;
                    updateGallery();
                }
            });

            List<PuzzleFactoryManager.Folder> path = new ArrayList<>();

            PuzzleFactoryManager.Folder folder = puzzleFactoryManager.getFolder(currentFolder);
            path.add(folder);
            UUID parent = folder.getParentFolderUuid();

            while (parent != PuzzleFactoryManager.rootFolderUuid) {
                folder = puzzleFactoryManager.getFolder(parent);
                path.add(folder);
                parent = folder.getParentFolderUuid();
            }

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                for (int i = path.size() - 1; i >= 0; i--) {
                    View view = inflater.inflate(R.layout.folder_navigation_element, folderNavigator, true);

                    final UUID folderUuid = path.get(i).getUuid();
                    TextView textView = view.findViewById(R.id.folder_name);
                    textView.setText(path.get(i).getName());
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentFolder = folderUuid;
                            updateGallery();
                        }
                    });
                }
            }
        } else {
            folderNavigator.setVisibility(View.GONE);
        }
    }

    public static Bitmap getNotLoadedBitmap(){
        if(notLoadedBitmap == null){
            notLoadedBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(notLoadedBitmap);
            canvas.drawColor(Color.GRAY);
            canvas.drawBitmap(notLoadedBitmap, 0, 0, null);
        }
        return notLoadedBitmap;
    }

    @Override
    public void onBackPressed() {
        if (currentFolder == PuzzleFactoryManager.rootFolderUuid)
            finish();
        else {
            PuzzleFactoryManager.Folder folder = puzzleFactoryManager.getFolder(currentFolder);
            currentFolder = folder.getParentFolderUuid();
            updateGallery();
        }
    }
}
