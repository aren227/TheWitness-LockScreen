package com.aren.thewitnesspuzzle.puzzle.base.rules;

import org.json.JSONObject;

public class EndingPointRule extends RuleBase {

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
