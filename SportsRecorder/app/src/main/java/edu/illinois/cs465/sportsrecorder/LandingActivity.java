package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class LandingActivity extends Activity implements View.OnClickListener {

    LinearLayout savedGamesButton, playersButton, newGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        getActionBar().hide();

        savedGamesButton = (LinearLayout) findViewById(R.id.savedGames);
        playersButton = (LinearLayout) findViewById(R.id.savedPlayers);
        newGameButton = (LinearLayout) findViewById(R.id.newGame);

        savedGamesButton.setOnClickListener(this);
        playersButton.setOnClickListener(this);
        newGameButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent resultIntent;
        if (v.getId() == R.id.savedGames) {
            resultIntent = new Intent(LandingActivity.this, SavedGames.class);
            startActivity(resultIntent);
        } else if (v.getId() == R.id.savedPlayers) {
            resultIntent = new Intent(LandingActivity.this, PlayerActivity.class);
            startActivity(resultIntent);
        } else if (v.getId() == R.id.newGame) {
            resultIntent = new Intent(LandingActivity.this, GameSetupActivity.class);
            startActivity(resultIntent);
        }
    }
}
