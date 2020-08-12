package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.EliminationMark;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;

import java.util.List;

public class Elimination extends Rule {

    public static final int COLOR = android.graphics.Color.parseColor("#fafafa");

    public Elimination(){
        super();
    }

    @Override
    public Shape getShape(){
        if(!(getGraphElement() instanceof Tile)) return null;
        return new EliminationMark(new Vector3(getGraphElement().x, getGraphElement().y, 0), COLOR);
    }

    @Override
    public boolean canValidateLocally(){
        return false;
    }

}
