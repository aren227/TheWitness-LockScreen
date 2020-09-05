package com.aren.thewitnesspuzzle.puzzle.sound;

import com.aren.thewitnesspuzzle.R;

public enum Sounds {

    ERASER_APPLY(R.raw.eraser_apply),
    ABORT_TRACING(R.raw.panel_abort_tracing),
    FAILURE(R.raw.panel_failure),
    POTENTIAL_FAILURE(R.raw.panel_potential_failure),
    FINISH_TRACING(R.raw.panel_finish_tracing),
    START_TRACING(R.raw.panel_start_tracing),
    SUCCESS(R.raw.panel_success);

    int id;

    Sounds(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
