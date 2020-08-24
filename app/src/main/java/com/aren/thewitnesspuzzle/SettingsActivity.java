package com.aren.thewitnesspuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.game.Game;

public class SettingsActivity extends AppCompatActivity {

    Game game;

    TextView soundsText;
    TextView holdingText;
    TextView shadowPanelText;
    TextView bloomText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        soundsText = findViewById(R.id.settings_sounds);
        holdingText = findViewById(R.id.settings_holding);
        shadowPanelText = findViewById(R.id.settings_shadow_panel);
        bloomText = findViewById(R.id.settings_bloom);

        game = new Game(this, false);

        updateSoundsText();
        updateHoldingText();
        updateShadowPanelText();
        updateBloomText();

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
}
