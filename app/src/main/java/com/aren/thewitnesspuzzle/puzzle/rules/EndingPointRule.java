package com.aren.thewitnesspuzzle.puzzle.rules;

import org.json.JSONObject;

public class EndingPointRule extends Rule {

    public static final String NAME = "end";

    public EndingPointRule() {
        super();
    }

    public EndingPointRule(JSONObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
