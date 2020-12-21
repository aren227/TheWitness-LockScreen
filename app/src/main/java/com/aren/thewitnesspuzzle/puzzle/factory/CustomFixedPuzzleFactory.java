package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.base.PuzzleBase;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.UUID;

public class CustomFixedPuzzleFactory extends PuzzleFactory {

    public enum Source { RANDOM, ME, OTHER, UNKNOWN }

    public CustomFixedPuzzleFactory(Context context, UUID uuid) {
        super(context, uuid);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        try {
            JSONObject jsonObject = new JSONObject(getConfig().getString("content", "{}"));
            return new PuzzleRenderer(game, PuzzleBase.deserialize(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.fromString(getConfig().getString("difficulty", "ALWAYS_SOLVABLE"));
    }

    @Override
    public String getName() {
        return getConfig().getString("name", "No Name");
    }

    @Override
    public boolean isCreatedByUser() {
        return true;
    }

    public Source getSource() {
        return Source.valueOf(getConfig().getString("source", "UNKNOWN"));
    }

    public void setLiked(PuzzleBase puzzleBase) throws JSONException {
        getConfig().setFactoryType("fixed");
        getConfig().setString("name", "Liked Puzzle");
        getConfig().setString("source", "RANDOM");
        getConfig().setString("content", puzzleBase.serialize().toString());
        getConfig().save();
    }
}
