package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.os.Bundle;

public class RecordGameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_game);
        getActionBar().hide();
    }
}
