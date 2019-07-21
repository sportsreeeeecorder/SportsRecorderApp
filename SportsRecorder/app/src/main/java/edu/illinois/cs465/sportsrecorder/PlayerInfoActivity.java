package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class PlayerInfoActivity extends Activity {

    final int ADD_PLAYER_INFO = 0, ADD_PLAYER_GAME = 1;
    String statsNameLookup[] = {"Assists", "Ones", "Twos", "Threes", "Turnovers", "Rebounds", "Fouls"};

    DatabaseReference savedPlayerInfoRef, gameInfoRef;
    LinearLayout.LayoutParams layoutParams;
    LinearLayout savedPlayerInfoLayout, savedPlayerGamesLayout;

    TextView playerNameView, playerGamesView;
    String playerID, currentGameRef;

    int unknownGames = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);
        getActionBar().hide();

        playerID = getIntent().getStringExtra(getString(R.string.intentPlayerID));

        savedPlayerInfoLayout = (LinearLayout) findViewById(R.id.playersStatsLayout);
        savedPlayerGamesLayout = (LinearLayout) findViewById(R.id.playerGamesLayout);

        playerNameView = (TextView) findViewById(R.id.playerNameView);
        playerGamesView = (TextView) findViewById(R.id.playerGamesTextView);

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        gameInfoRef = FirebaseDatabase.getInstance().getReference().child("games");

        savedPlayerInfoRef = FirebaseDatabase.getInstance().getReference().child("players").child(playerID);
        savedPlayerInfoRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                String branch = dataSnapshot.getKey();
                if(branch.equals("career_stats")) {
                    Log.d("LOG", "FIREBASE: about to add player info from snapshot: " + dataSnapshot.toString());
                    for(int i = 0; i < 7; i++) {
                        generateUI(statsNameLookup[i], ((ArrayList<Long>) dataSnapshot.getValue()).get(i).toString(), ADD_PLAYER_INFO);
                    }
                } else if (branch.equals("games_played")) {
                    for(DataSnapshot stat : dataSnapshot.getChildren()) {
                        currentGameRef = stat.getKey();
                        Log.d("LOG", "FIREBASE: Looking for game name for ID = " + currentGameRef);
                        gameInfoRef.child(currentGameRef).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot nameDataSnapshot) {
                                try {
                                    generateUI(nameDataSnapshot.getValue().toString(), nameDataSnapshot.getRef().getParent().getKey(), ADD_PLAYER_GAME);
                                } catch (NullPointerException e) {
                                    unknownGames++;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError nameDatabaseError) {

                            }
                        });
                    }
                } else if (branch.equals("player_name")) {
                    playerNameView.setText(dataSnapshot.getValue().toString() + "'s Stats");
                    playerGamesView.setText(dataSnapshot.getValue().toString() + "'s Games");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String branch = dataSnapshot.getKey();
                if(branch.equals("career_stats")) {
                    Log.d("LOG", "FIREBASE: about to add player info from snapshot: " + dataSnapshot.toString());
                    for(int i = 0; i < 7; i++) {
                        generateUI(statsNameLookup[i], ((ArrayList<Long>) dataSnapshot.getValue()).get(i).toString(), ADD_PLAYER_INFO);
                    }
                }
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
                String intentGameID = ((TextView) v).getHint().toString();
                Intent switchToViewActivity = new Intent(PlayerInfoActivity.this, GameStatsActivity.class);
                switchToViewActivity.putExtra(getString(R.string.intentGameID), intentGameID);
                startActivity(switchToViewActivity);
                finish();
            }
        }
    };

    private void generateUI(String pName, String pID, int pInfoType) {
        TextView textView = new TextView(PlayerInfoActivity.this);

        if(pInfoType == ADD_PLAYER_INFO) {
            savedPlayerInfoLayout.addView(textView);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(20, 25, 20, 25);
            textView.setText(pName + ": " + pID);
            textView.setHapticFeedbackEnabled(true);
        } else if(pInfoType == ADD_PLAYER_GAME) {
            savedPlayerGamesLayout.addView(textView);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(20, 25, 20, 25);
            textView.setText(pName);
            textView.setHint(pID);
            textView.setHapticFeedbackEnabled(true);
            textView.setOnClickListener(playerClickListener);
        }
    }
}
