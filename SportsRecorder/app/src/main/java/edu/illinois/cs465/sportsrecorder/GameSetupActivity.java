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
    String gameCode;
    TextView gameCodeView;
    EditText locationField, nameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

        UserInfo.gameCode = generateCode(4);
        gameCode = UserInfo.gameCode;

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

                gameRef.child("users").child(UserInfo.userId).child("saved_games").child(gameCode).setValue(nameField.getText().toString());

                Intent gameToHomeIntent = new Intent(GameSetupActivity.this, HomeSetupActivity.class);
                startActivity(gameToHomeIntent);
                finish();
            }
        });
    }

    String generateCode(int pLength) {
        String retString = "";
        String alphaNums[] = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

        for(int i = 0; i < pLength; i++) {
            retString += alphaNums[(int) (Math.random()*alphaNums.length)];
        }

        return retString;
    }

}
