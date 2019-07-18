package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeSetupActivity extends Activity {

    Button homeToAwayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_setup);

        getActionBar().hide();

        homeToAwayButton = (Button) findViewById(R.id.homeToAway);
        homeToAwayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeToAwayIntent = new Intent(HomeSetupActivity.this, AwaySetupActivity.class);
                startActivity(homeToAwayIntent);
                finish();
            }
        });
    }
}
