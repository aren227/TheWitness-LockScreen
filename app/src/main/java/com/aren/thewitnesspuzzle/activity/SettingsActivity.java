package com.aren.thewitnesspuzzle.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.game.Game;

public class SettingsActivity extends AppCompatActivity {

    Game game;

    TextView soundsText;
    TextView holdingText;
    TextView shadowPanelText;
    TextView bloomText;
    TextView lockDelayText;

    private int[] lockDelays = new int[]{0, 10, 30, 60, 60 * 15};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        soundsText = findViewById(R.id.settings_sounds);
        holdingText = findViewById(R.id.settings_holding);
        shadowPanelText = findViewById(R.id.settings_shadow_panel);
        bloomText = findViewById(R.id.settings_bloom);
        lockDelayText = findViewById(R.id.settings_lockdelay);

        game = new Game(this, Game.Mode.PLAY);

        updateSoundsText();
        updateHoldingText();
        updateShadowPanelText();
        updateBloomText();
        updateLockDelayText();

        soundsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.getSettings().setSoundsEnabled(!game.getSettings().getSoundsEnabled());
                updateSoundsText();
            }
        });

        holdingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.getSettings().setHoldingPuzzles(!game.getSettings().getHoldingPuzzles());
                updateHoldingText();
            }
        });

        shadowPanelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.getSettings().setShadowPanelEnabled(!game.getSettings().getShadowPanelEnabled());
                updateShadowPanelText();
            }
        });

        bloomText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.getSettings().setBloomEnabled(!game.getSettings().getBloomEnabled());
                updateBloomText();
            }
        });

        lockDelayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idx = 0;
                int current = game.getSettings().getLockDelay();
                for(int i = 0; i < lockDelays.length; i++){
                    if(lockDelays[i] == current){
                        idx = i;
                        break;
                    }
                }
                game.getSettings().setLockDelay(lockDelays[(idx + 1) % lockDelays.length]);
                updateLockDelayText();
            }
        });
    }

    private void updateSoundsText(){
        soundsText.setText(game.getSettings().getSoundsEnabled() ? "Yes" : "No");
    }

    private void updateHoldingText(){
        holdingText.setText(game.getSettings().getHoldingPuzzles() ? "Yes" : "No");
    }

    private void updateShadowPanelText(){
        shadowPanelText.setText(game.getSettings().getShadowPanelEnabled() ? "Yes" : "No");
    }

    private void updateBloomText(){
        bloomText.setText(game.getSettings().getBloomEnabled() ? "Yes" : "No");
    }

    private void updateLockDelayText(){
        int delay = game.getSettings().getLockDelay();
        String str = "";
        if(delay == 0){
            str = "Instantly";
        }
        else if(delay < 60){
            str = delay + "s";
        }
        else{
            str = delay / 60 + "m";
        }
        lockDelayText.setText(str);
    }
}
