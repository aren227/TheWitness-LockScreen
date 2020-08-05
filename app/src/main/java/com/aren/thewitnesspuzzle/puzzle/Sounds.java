package com.aren.thewitnesspuzzle.puzzle;

import com.aren.thewitnesspuzzle.R;

public enum Sounds {

    ABORT_TRACE(R.raw.panel_abort_tracing), FAILURE(R.raw.panel_failure), FINISH_TRACE(R.raw.panel_finish_tracing), START_TRACE(R.raw.panel_start_tracing), SUCCESS(R.raw.panel_success1);

    int id;

    Sounds(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

}
