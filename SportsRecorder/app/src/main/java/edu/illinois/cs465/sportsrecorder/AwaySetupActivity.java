package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AwaySetupActivity extends Activity {

    Button awayToRecord;
    DatabaseReference gameRef;
    String gameCode = UserInfo.gameCode;
    TextView colorView;
    EditText nameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_away_setup);

        getActionBar().hide();
        gameRef = FirebaseDatabase.getInstance().getReference();

        nameField = (EditText) findViewById(R.id.awayNameField);
        colorView = (TextView) findViewById(R.id.awayColorField);

        awayToRecord = (Button) findViewById(R.id.awayToRecord);
        awayToRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!" ".equals(nameField.getText().toString() + " ")) {
                    gameRef.child("games").child(gameCode).child("away_properties").child("name").setValue(nameField.getText().toString());
                } else {
                    gameRef.child("games").child(gameCode).child("away_properties").child("name").setValue("Away");
                }

                gameRef.child("games").child(gameCode).child("away_properties").child("color").setValue(colorView.getText().toString().replace("Color: ", ""));

                Intent awayToRecordIntent = new Intent(AwaySetupActivity.this, RecordGameActivity.class);
                startActivity(awayToRecordIntent);
                finish();
            }
        });
    }
}
