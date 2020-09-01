package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.HexagonPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.EdgeProportion;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CustomPatternPuzzleFactory extends PuzzleFactory {

    public CustomPatternPuzzleFactory(Context context, UUID uuid) {
        super(context, uuid);
    }

    @Override
    public Puzzle generate(Game game, Random random) {
        return generateWithPattern(game, random, false);
    }

    public Puzzle generateWithPattern(Game game, Random random, boolean showPattern){
        Puzzle puzzle = null;

        String puzzleType = getConfig().getString("puzzleType", "null");
        PuzzleColorPalette palette = getConfig().getColorPalette("color", PalettePreset.get("Entry_1"));

        if(puzzleType.equals("grid")){
            if(!getConfig().containsKey("width") || !getConfig().containsKey("height")) return null;
            int width = getConfig().getInt("width", 4);
            int height = getConfig().getInt("height", 4);
            puzzle = new GridPuzzle(game, palette, width, height);
            ((GridPuzzle)puzzle).addStartingPoint(0, 0);
            ((GridPuzzle)puzzle).addEndingPoint(width, height);
        }
        else if(puzzleType.equals("hexagon")){
            puzzle = new HexagonPuzzle(game, palette);
        }
        else{
            return null;
        }

        if(!getConfig().containsKey("pattern")) return null;

        List<Integer> pattern = getConfig().getIntList("pattern", new ArrayList<Integer>());
        puzzle.setCustomPattern(pattern);

        if(showPattern){
            ArrayList<Vertex> vertices = new ArrayList<>();
            for(int i : pattern){
                vertices.add(puzzle.getVertex(i));
            }
            EdgeProportion lastEdge = null;
            for(Edge edge : puzzle.getEdges()){
                if(edge.from == vertices.get(vertices.size() - 1) && edge.to.getRule() instanceof EndingPointRule){
                    lastEdge = new EdgeProportion(edge);
                    lastEdge.proportion = 1f;
                    break;
                }
                if(edge.to == vertices.get(vertices.size() - 1) && edge.from.getRule() instanceof EndingPointRule){
                    lastEdge = new EdgeProportion(edge);
                    lastEdge.reverse = true;
                    lastEdge.proportion = 1f;
                    break;
                }
            }

            Cursor cursor = new Cursor(puzzle, vertices, lastEdge);

            puzzle.setCursor(cursor);
        }

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty(){
        return Difficulty.CUSTOM_PATTERN;
    }

    @Override
    public String getName() {
        return getConfig().getString("name", "No Name");
    }

    @Override
    public boolean isCreatedByUser(){
        return true;
    }
}
