package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AwaySetupActivity extends Activity {

    Button awayToRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_away_setup);

        getActionBar().hide();

        awayToRecord = (Button) findViewById(R.id.awayToRecord);
        awayToRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent awayToRecordIntent = new Intent(AwaySetupActivity.this, RecordGameActivity.class);
                startActivity(awayToRecordIntent);
                finish();
            }
        });
    }
}
