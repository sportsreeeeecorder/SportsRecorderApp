package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import androidx.annotation.NonNull;

public class SavedGames extends Activity {

    DatabaseReference savedGamesRef;
    LinearLayout savedGamesLayout;
    LinearLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_games);
        getActionBar().hide();

        savedGamesLayout = (LinearLayout) findViewById(R.id.savedGamesList);

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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

        savedGamesLayout.addView(textView);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(20, 25, 20, 25);
        textView.setText(pName);
        textView.setHint(pID);
        textView.setHapticFeedbackEnabled(true);
        textView.setOnClickListener(gameClickListener);
    }
}
