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

public class GameSetupActivity extends Activity {

    Button gameToHomeButton;
    DatabaseReference gameRef;
    String gameCode = UserInfo.gameCode;
    TextView gameCodeView;
    EditText locationField, nameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

        gameCodeView = (TextView) findViewById(R.id.gameCode);
        gameCodeView.setText("Code: " + gameCode);

        locationField = (EditText) findViewById(R.id.locationField);
        nameField = (EditText) findViewById(R.id.gameNameField);

        getActionBar().hide();
        gameRef = FirebaseDatabase.getInstance().getReference();

        gameToHomeButton = (Button) findViewById(R.id.gameToHome);
        gameToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!" ".equals(nameField.getText().toString() + " ")) {
                    gameRef.child("games").child(gameCode).child("name").setValue(nameField.getText().toString());
                }

                if(!" ".equals(locationField.getText().toString() + " ")) {
                    gameRef.child("games").child(gameCode).child("location").setValue(locationField.getText().toString());
                }

                gameRef.child("users").child(UserInfo.userId).child("saved_games").child("1").setValue(gameCode);

                Intent gameToHomeIntent = new Intent(GameSetupActivity.this, HomeSetupActivity.class);
                startActivity(gameToHomeIntent);
                finish();
            }
        });
    }
}
