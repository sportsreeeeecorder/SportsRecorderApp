package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.os.Bundle;

public class PlayerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getActionBar().hide();
    }
}
