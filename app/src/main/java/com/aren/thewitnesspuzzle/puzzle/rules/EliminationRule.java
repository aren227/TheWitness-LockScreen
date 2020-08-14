package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.shape.EliminatorShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;

public class EliminationRule extends Rule {

    public static final int COLOR = android.graphics.Color.parseColor("#fafafa");

    public EliminationRule(){
        super();
    }

    @Override
    public Shape generateShape(){
        if(!(getGraphElement() instanceof Tile)) return null;
        return new EliminatorShape(new Vector3(getGraphElement().x, getGraphElement().y, 0), COLOR);
    }

    @Override
    public boolean canValidateLocally(){
        return false;
    }

}
