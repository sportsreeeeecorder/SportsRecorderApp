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

public class HomeSetupActivity extends Activity {

    Button homeToAwayButton;
    DatabaseReference gameRef;
    String gameCode = UserInfo.gameCode;
    TextView colorView;
    EditText nameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_setup);

        getActionBar().hide();
        gameRef = FirebaseDatabase.getInstance().getReference();

        nameField = (EditText) findViewById(R.id.homeNameField);
        colorView = (TextView) findViewById(R.id.homeColorField);

        homeToAwayButton = (Button) findViewById(R.id.homeToAway);
        homeToAwayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!" ".equals(nameField.getText().toString() + " ")) {
                    gameRef.child("games").child(gameCode).child("home_properties").child("name").setValue(nameField.getText().toString());
                } else {
                    gameRef.child("games").child(gameCode).child("home_properties").child("name").setValue("Home");
                }

                gameRef.child("games").child(gameCode).child("home_properties").child("color").setValue(colorView.getText().toString().replace("Color: ", ""));

                Intent homeToAwayIntent = new Intent(HomeSetupActivity.this, AwaySetupActivity.class);
                startActivity(homeToAwayIntent);
                finish();
            }
        });
    }
}