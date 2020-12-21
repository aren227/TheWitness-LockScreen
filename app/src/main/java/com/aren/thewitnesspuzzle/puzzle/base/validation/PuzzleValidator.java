package com.aren.thewitnesspuzzle.puzzle.base.validation;

import com.aren.thewitnesspuzzle.puzzle.base.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.base.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.puzzle.base.PuzzleBase;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.base.rules.RuleBase;

import java.util.ArrayList;
import java.util.List;

public class PuzzleValidator {

    public static ValidationResult validate(Cursor cursor, PuzzleBase puzzleBase) {
        if (puzzleBase instanceof GridPuzzle) {
            GridAreaSplitter splitter = new GridAreaSplitter(cursor);
            ValidationResult result = new ValidationResult();
            for (Area area : splitter.areaList) {
                result.areaValidationResults.add(area.validate(cursor));
            }

            //FIXME: Dirty code again. I think getVisitedVerticies() of SymmetryCursor should return with opposite vertices.
            List<RuleBase> rules = new ArrayList<>();
            for (Vertex vertex : cursor.getVisitedVertices()) {
                if (vertex.getRule() != null) rules.add(vertex.getRule());
                if (puzzleBase instanceof GridSymmetryPuzzle) {
                    Vertex opposite = ((GridSymmetryPuzzle) puzzleBase).getOppositeVertex(vertex);
                    if (opposite.getRule() != null) rules.add(opposite.getRule());
                }
            }
            for (Edge edge : cursor.getFullyVisitedEdges()) {
                if (edge.getRule() != null) rules.add(edge.getRule());
                if (puzzleBase instanceof GridSymmetryPuzzle) {
                    Edge opposite = ((GridSymmetryPuzzle) puzzleBase).getOppositeEdge(edge);
                    if (opposite.getRule() != null) rules.add(opposite.getRule());
                }
            }

            for (RuleBase rule : rules) {
                if (!rule.validateLocally(cursor)) {
                    result.notOnAreaErrors.add(rule);
                }
            }

            return result;
        } else {
            ValidationResult result = new ValidationResult();
            //TODO: Support area validation
            for (RuleBase rule : puzzleBase.getAllRules()) {
                if (!rule.validateLocally(cursor)) {
                    result.notOnAreaErrors.add(rule);
                }
            }
            return result;
        }
    }

    public static boolean validate(Cursor cursor, List<Integer> pattern) {
        List<Integer> visited = cursor.getVisitedVertexIndices();
        if (pattern.size() != visited.size()) {
            return false;
        }
        for (int i = 0; i < pattern.size(); i++) {
            if (!pattern.get(i).equals(visited.get(i))) {
                return false;
            }
        }
        return true;
    }

}
