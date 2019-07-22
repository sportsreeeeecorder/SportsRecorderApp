package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import androidx.annotation.NonNull;

public class PlayerActivity extends Activity {

    DatabaseReference savedPlayersRef, newPlayerRef;
    LinearLayout savedPlayersLayout;
    LinearLayout.LayoutParams layoutParams;
    Button addPlayerPlus, addPlayerSubmit;
    EditText playerNameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getActionBar().hide();

        savedPlayersLayout = (LinearLayout) findViewById(R.id.savedPlayersList);

        addPlayerPlus = (Button) findViewById(R.id.addPlayerPlus);
        playerNameField = (EditText) findViewById(R.id.addPlayerField);
        addPlayerSubmit = (Button) findViewById(R.id.addPlayerSubmit);

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        addPlayerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerNameField.getText().toString().length() == 0) {
                    Toast.makeText(PlayerActivity.this, "Player field cannot be blank!", Toast.LENGTH_SHORT).show();
                    return;
                } else if(playerNameField.getText().toString().length() != 5) {
                    String generatedCode = generateCode(5);
                    addPlayerSubmit.setText("Loading data...");
                    newPlayerRef = FirebaseDatabase.getInstance().getReference();
                    newPlayerRef.child("players").child(generatedCode).child("player_name").setValue(playerNameField.getText().toString());
                    for(int i = 0; i < 7; i++) {
                        newPlayerRef.child("players").child(generatedCode).child("career_stats").child(""+i).setValue(0);
                    }
                    newPlayerRef.child("users").child(UserInfo.userId).child("saved_players").child(generatedCode).setValue(playerNameField.getText().toString());
                    Intent switchToViewActivity = new Intent(PlayerActivity.this, PlayerInfoActivity.class);
                    switchToViewActivity.putExtra(getString(R.string.intentPlayerID), generatedCode);
                    startActivity(switchToViewActivity);
                    finish();
                }
                newPlayerRef = FirebaseDatabase.getInstance().getReference().child("players").child(playerNameField.getText().toString());
                newPlayerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            for(DataSnapshot item: dataSnapshot.getChildren()) {
                                if(item.getKey().equals("player_name")) {
                                    newPlayerRef.getParent().getParent().child("users").child(UserInfo.userId).child("saved_players").child(playerNameField.getText().toString()).setValue(item.getValue());
                                    Intent switchToViewActivity = new Intent(PlayerActivity.this, PlayerInfoActivity.class);
                                    switchToViewActivity.putExtra(getString(R.string.intentPlayerID), playerNameField.getText().toString());
                                    startActivity(switchToViewActivity);
                                    finish();
                                }
                            }
                        } catch (NullPointerException e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        addPlayerPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerNameField.setVisibility(View.VISIBLE);
                addPlayerSubmit.setVisibility(View.VISIBLE);

                addPlayerPlus.setVisibility(View.GONE);
            }
        });

        savedPlayersRef = FirebaseDatabase.getInstance().getReference().child("users").child(UserInfo.userId).child("saved_players");
        savedPlayersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                generateUI( dataSnapshot.getValue().toString(), dataSnapshot.getKey().toString() );
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    View.OnClickListener playerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof TextView) {
                String intentPlayerID = ((TextView) v).getHint().toString();
                Log.d("OKAY SO", "ID is"  + intentPlayerID);
                Intent switchToViewActivity = new Intent(PlayerActivity.this, PlayerInfoActivity.class);
                switchToViewActivity.putExtra(getString(R.string.intentPlayerID), intentPlayerID);
                startActivity(switchToViewActivity);
                finish();
            }
        }
    };

    private void generateUI(String pName, String pID) {
        TextView textView = new TextView(PlayerActivity.this);
        View greyPadding = new View(PlayerActivity.this);

        savedPlayersLayout.addView(textView);
        savedPlayersLayout.addView(greyPadding);

        textView.setLayoutParams(layoutParams);
        textView.setPadding(20, 25, 20, 25);
        textView.setText(pName + " - " + pID);
        textView.setHint(pID);
        textView.setHapticFeedbackEnabled(true);
        textView.setOnClickListener(playerClickListener);

        greyPadding.setLayoutParams(layoutParams);
        greyPadding.setBackgroundColor(Color.DKGRAY);
        greyPadding.setMinimumHeight(1);
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
