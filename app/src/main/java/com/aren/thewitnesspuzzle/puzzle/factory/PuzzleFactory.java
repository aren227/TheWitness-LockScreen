package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;
import android.graphics.Bitmap;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class PuzzleFactory {

    private Bitmap thumbnailCache;
    private PuzzleFactoryConfig config;

    private List<Runnable> onPreviewRenderedCallbackQueue;

    public PuzzleFactory(Context context) {
        this(context, null);
    }

    public PuzzleFactory(Context context, UUID uuid) {
        if (uuid == null) {
            uuid = UUID.nameUUIDFromBytes(getClass().getName().getBytes());
        }
        config = new PuzzleFactoryConfig(context, uuid);

        onPreviewRenderedCallbackQueue = new ArrayList<>();
    }

    public abstract PuzzleRenderer generate(Game game, Random random);

    public Difficulty getDifficulty() {
        return null;
    }

    public abstract String getName();

    public UUID getUuid() {
        return getConfig().factoryUuid;
    }

    public void setThumbnailCache(Bitmap bitmap) {
        thumbnailCache = bitmap;
        for(Runnable runnable : onPreviewRenderedCallbackQueue){
            runnable.run();
        }
        onPreviewRenderedCallbackQueue.clear();
    }

    public Bitmap getThumbnailCache() {
        return thumbnailCache;
    }

    public void clearThumbnailCache() {
        thumbnailCache = null;
    }

    public PuzzleFactoryConfig getConfig() {
        return config;
    }

    public boolean isCreatedByUser() {
        return false;
    }

    public void setOnPreviewRendered(Runnable onPreviewRendered){
        onPreviewRenderedCallbackQueue.add(onPreviewRendered);
    }
}
