package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class SavedGames extends Activity {

    DatabaseReference savedGamesRef, newGameRef;
    LinearLayout savedGamesLayout;
    LinearLayout.LayoutParams layoutParams;
    Button addGamePlus, addGameSubmit;
    EditText gameNameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_games);
        getActionBar().hide();

        savedGamesLayout = (LinearLayout) findViewById(R.id.savedGamesList);

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        addGamePlus = (Button) findViewById(R.id.addGamePlus);
        gameNameField = (EditText) findViewById(R.id.addGameField);
        addGameSubmit = (Button) findViewById(R.id.addGameSubmit);

        addGameSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameNameField.length() != 4) {
                    Toast.makeText(SavedGames.this, "Wrong code, please try again!", Toast.LENGTH_SHORT).show();
                    return;
                }
                newGameRef = FirebaseDatabase.getInstance().getReference().child("games").child(gameNameField.getText().toString());
                newGameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            for(DataSnapshot item: dataSnapshot.getChildren()) {
                                if(item.getKey().equals("name")) {
                                    newGameRef.getParent().getParent().child("users").child(UserInfo.userId).child("saved_games").child(gameNameField.getText().toString()).setValue(item.getValue());
                                    Intent switchToViewActivity = new Intent(SavedGames.this, GameStatsActivity.class);
                                    switchToViewActivity.putExtra(getString(R.string.intentGameID), gameNameField.getText().toString());
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

        addGamePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameNameField.setVisibility(View.VISIBLE);
                addGameSubmit.setVisibility(View.VISIBLE);

                addGamePlus.setVisibility(View.GONE);
            }
        });

        savedGamesRef = FirebaseDatabase.getInstance().getReference().child("users").child(UserInfo.userId).child("saved_games");
        savedGamesRef.addChildEventListener(new ChildEventListener() {
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

    View.OnClickListener gameClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof TextView) {
                String intentGameID = ((TextView) v).getHint().toString();
                Intent switchToViewActivity = new Intent(SavedGames.this, GameStatsActivity.class);
                switchToViewActivity.putExtra(getString(R.string.intentGameID), intentGameID);
                startActivity(switchToViewActivity);
                finish();
            }
        }
    };

    private void generateUI(String pName, String pID) {
        TextView textView = new TextView(SavedGames.this);
        View greyPadding = new View(SavedGames.this);

        savedGamesLayout.addView(textView);
        savedGamesLayout.addView(greyPadding);

        textView.setLayoutParams(layoutParams);
        textView.setPadding(20, 25, 20, 25);
        textView.setText(pName + " - " + pID);
        textView.setHint(pID);
        textView.setHapticFeedbackEnabled(true);
        textView.setOnClickListener(gameClickListener);

        greyPadding.setLayoutParams(layoutParams);
        greyPadding.setBackgroundColor(Color.DKGRAY);
        greyPadding.setMinimumHeight(1);
    }
}
