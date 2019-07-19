package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.os.Bundle;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SavedGames extends Activity {

    DatabaseReference savedGamesRef;
    String gameCode = UserInfo.gameCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_games);
        getActionBar().hide();

        savedGamesRef = FirebaseDatabase.getInstance().getReference().child("users").child(UserInfo.userId).child("saved_games");

        savedGamesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@android.support.annotation.NonNull DataSnapshot dataSnapshot, @android.support.annotation.Nullable String s) {

            }

            @Override
            public void onChildChanged(@android.support.annotation.NonNull DataSnapshot dataSnapshot, @android.support.annotation.Nullable String s) {

            }

            @Override
            public void onChildRemoved(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@android.support.annotation.NonNull DataSnapshot dataSnapshot, @android.support.annotation.Nullable String s) {

            }

            @Override
            public void onCancelled(@android.support.annotation.NonNull DatabaseError databaseError) {

            }
        });
    }
}
