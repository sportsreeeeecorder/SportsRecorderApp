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
import com.google.firebase.database.annotations.Nullable;

import androidx.annotation.NonNull;

public class PlayerActivity extends Activity {

    DatabaseReference savedPlayersRef;
    LinearLayout savedPlayersLayout;
    LinearLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getActionBar().hide();

        savedPlayersLayout = (LinearLayout) findViewById(R.id.savedPlayersList);

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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

        savedPlayersLayout.addView(textView);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(20, 25, 20, 25);
        textView.setText(pName);
        textView.setHint(pID);
        textView.setHapticFeedbackEnabled(true);
        textView.setOnClickListener(playerClickListener);
    }
}
