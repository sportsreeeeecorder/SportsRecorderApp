package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;

public class HomeSetupActivity extends Activity {

    Button homeToAwayButton, loadHomePlayerListButton;
    DatabaseReference gameRef, myPlayersRef;
    String gameCode = UserInfo.gameCode;
    TextView colorView;
    EditText nameField;

    LinearLayout.LayoutParams layoutParams;
    LinearLayout addHomePlayerList;

    HashMap<String, String> userLookupList;
    ArrayList<String> selectedPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_setup);

        getActionBar().hide();
        gameRef = FirebaseDatabase.getInstance().getReference();

        userLookupList = new HashMap<>();
        selectedPlayers = new ArrayList<>();

        nameField = (EditText) findViewById(R.id.homeNameField);
        colorView = (TextView) findViewById(R.id.homeColorField);
        addHomePlayerList = (LinearLayout) findViewById(R.id.addHomePlayerList);

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        myPlayersRef = FirebaseDatabase.getInstance().getReference().child("users").child(UserInfo.userId).child("saved_players");
        myPlayersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for(DataSnapshot player: dataSnapshot.getChildren()) {
                        userLookupList.put(player.getValue().toString(), player.getKey());
                        generateUI(player.getKey(), player.getValue().toString());
                    }
                } catch (NullPointerException e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        homeToAwayButton = (Button) findViewById(R.id.homeToAway);
        loadHomePlayerListButton = (Button) findViewById(R.id.loadHomePlayerListButton);

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
                for(int i = 0; i < selectedPlayers.size(); i++) {
                    for(int j = 0; j < 7; j++) {
                        gameRef.child("games").child("players").child("home").child(selectedPlayers.get(i)).child(j).setValue(0);
                    }
                }
                homeToAwayIntent.putExtra(getString(R.string.intentHomePlayersList), selectedPlayers.toArray());
                startActivity(homeToAwayIntent);
                finish();
            }
        });

        loadHomePlayerListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHomePlayerList.setVisibility(View.VISIBLE);
            }
        });
    }

    View.OnClickListener playerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof TextView) {
                String playerSelected = ((TextView) v).getHint().toString();
                v.setVisibility(View.GONE);
                selectedPlayers.add(playerSelected);
            }
        }
    };

    void generateUI(String pName,String pID) {
        TextView textView = new TextView(HomeSetupActivity.this);

        addHomePlayerList.addView(textView);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(20, 25, 20, 25);
        textView.setText(pName);
        textView.setHint(pID);
        textView.setHapticFeedbackEnabled(true);
        textView.setOnClickListener(playerClickListener);
    }
}