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

public class AwaySetupActivity extends Activity {

    Button awayToRecord;
    DatabaseReference gameRef, myPlayersRef;
    String gameCode = UserInfo.gameCode;
    EditText nameField;

    LinearLayout.LayoutParams layoutParams;
    LinearLayout addAwayPlayerList;

    HashMap<String, String> userLookupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_away_setup);

        getActionBar().hide();
        gameRef = FirebaseDatabase.getInstance().getReference();

        userLookupList = new HashMap<>();
        UserInfo.awayList = new ArrayList<>();

        nameField = (EditText) findViewById(R.id.awayNameField);
        addAwayPlayerList = (LinearLayout) findViewById(R.id.addAwayPlayerList);

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        myPlayersRef = FirebaseDatabase.getInstance().getReference().child("users").child(UserInfo.userId).child("saved_players");
        myPlayersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for(DataSnapshot player: dataSnapshot.getChildren()) {
                        if(!UserInfo.homeList.contains(player.getKey().toString())) {
                            userLookupList.put(player.getKey(), player.getValue().toString());
                            generateUI(player.getValue().toString(), player.getKey());
                        }
                    }
                } catch (NullPointerException e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        awayToRecord = (Button) findViewById(R.id.awayToRecord);

        awayToRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!" ".equals(nameField.getText().toString() + " ")) {
                    gameRef.child("games").child(gameCode).child("away_properties").child("name").setValue(nameField.getText().toString());
                } else {
                    gameRef.child("games").child(gameCode).child("away_properties").child("name").setValue("Away");
                }

                gameRef.child("games").child(gameCode).child("away_properties").child("color").setValue(getString(R.string.blue_hex));

                Intent awayToRecordIntent = new Intent(AwaySetupActivity.this, RecordGameActivity.class);
                if(!UserInfo.awayList.isEmpty()) {
                    for (int i = 0; i < UserInfo.awayList.size(); i++) {
                        for (int j = 0; j < 7; j++) {
                            gameRef.child("games").child(gameCode).child("players").child("away").child(UserInfo.awayList.get(i)).child("" + j).setValue(0);
                            gameRef.child("players").child(UserInfo.awayList.get(i)).child("games_played").child(gameCode).child("" + j).setValue(0);
                        }
                        UserInfo.codeNameLinkage.put(UserInfo.awayList.get(i), userLookupList.get(UserInfo.awayList.get(i)));
                    }
                }
                UserInfo.awayColor = getString(R.string.blue_hex);
                UserInfo.awayName = " ".equals(nameField.getText().toString() + " ")? "Away" : nameField.getText().toString();
                startActivity(awayToRecordIntent);
                finish();
            }
        });
    }

    View.OnClickListener playerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof TextView) {
                String playerSelected = ((TextView) v).getHint().toString();
                v.setVisibility(View.GONE);
                UserInfo.awayList.add(playerSelected);
            }
        }
    };

    void generateUI(String pName,String pID) {
        TextView textView = new TextView(AwaySetupActivity.this);

        addAwayPlayerList.addView(textView);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(20, 25, 20, 25);
        textView.setText(pName);
        textView.setHint(pID);
        textView.setHapticFeedbackEnabled(true);
        textView.setOnClickListener(playerClickListener);
    }
}
