package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;
import android.util.Log;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.SquareVertexRule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridTreeWalker;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class EntryAreaMazePuzzleFactory extends PuzzleFactory {
    public EntryAreaMazePuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public Puzzle generate(Game game, Random random) {
        Puzzle puzzle = new Puzzle(game, PalettePreset.get("Entry_1"));

        int width = 6;
        int height = 6;
        puzzle.setPathWidth(Math.min(width, height) * 0.05f + 0.05f);

        int startX = random.nextInt(width + 1);
        int startY = random.nextInt(height + 1);

        List<Vector2Int> branchCandidates = new ArrayList<>();
        for(int i = 0; i <= width; i++){
            for(int j = 0; j <= height; j++){
                int dist = Math.abs(startX - i) + Math.abs(startY - j);
                if(1 < dist && dist <= 3){
                    branchCandidates.add(new Vector2Int(i, j));
                }
            }
        }
        Collections.shuffle(branchCandidates, random);
        for(int i = 3; i < branchCandidates.size(); i++){
            branchCandidates.remove(i);
        }

        final RandomGridTreeWalker tree = new RandomGridTreeWalker(width, height, random, startX, startY, branchCandidates);

        Vertex[][] gridVertices = new Vertex[width + 1][height + 1];

        for(int i = 0; i <= width; i++){
            for(int j = 0; j <= height; j++){
                gridVertices[i][j] = puzzle.addVertex(new Vertex(puzzle, i, j));
            }
        }

        for(int i = 0; i <= width; i++){
            for(int j = 0; j <= height; j++){
                for(int k = 0; k < 4; k++){
                    if(((tree.direction[i][j] >> k) & 1) > 0){
                        Vertex from = gridVertices[i][j];
                        Vertex to = gridVertices[i + tree.delta[k][0]][j + tree.delta[k][1]];
                        puzzle.addEdge(new Edge(from, to));
                    }
                }
            }
        }

        gridVertices[startX][startY].setRule(new StartingPointRule());

        List<Vector2Int> endPointCandidates = new ArrayList<>();
        for(int x = 0; x <= width; x++){
            endPointCandidates.add(new Vector2Int(x, 0));
            endPointCandidates.add(new Vector2Int(x, height));
        }
        for(int y = 1; y < height; y++){
            endPointCandidates.add(new Vector2Int(0, y));
            endPointCandidates.add(new Vector2Int(width, y));
        }

        Collections.sort(endPointCandidates, new Comparator<Vector2Int>() {
            @Override
            public int compare(Vector2Int o1, Vector2Int o2) {
                return Integer.compare(tree.dist[o1.x][o1.y], tree.dist[o2.x][o2.y]);
            }
        });

        int idx = endPointCandidates.size() - random.nextInt(endPointCandidates.size() / 4) - 1;

        int endX = endPointCandidates.get(idx).x;
        int endY = endPointCandidates.get(idx).y;

        Vertex end = null;
        if(endY == 0){
            end = puzzle.addVertex(new Vertex(puzzle, endX, endY - puzzle.getPathWidth()), true);
        }
        else if(endY == height){
            end = puzzle.addVertex(new Vertex(puzzle, endX, endY + puzzle.getPathWidth()), true);
        }
        else if(endX == 0){
            end = puzzle.addVertex(new Vertex(puzzle, endX - puzzle.getPathWidth(), endY), true);
        }
        else if(endX == width){
            end = puzzle.addVertex(new Vertex(puzzle, endX + puzzle.getPathWidth(), endY), true);
        }

        puzzle.addEdge(new Edge(gridVertices[endX][endY], end));
        end.setRule(new EndingPointRule());

        for(int i = 0; i <= width; i++){
            for(int j = 0; j <= height; j++){
                if(tree.direction[i][j] == 0 && !(i == endX && j == endY)){
                    gridVertices[i][j].setRule(new SquareVertexRule());
                }
            }
        }

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.VERY_EASY;
    }

    @Override
    public String getName(){
        return "Entry Area #1";
    }
}
