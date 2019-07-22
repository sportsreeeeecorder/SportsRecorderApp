package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class GameStatsActivity extends Activity {

    final int ADD_Q1 = 1, ADD_Q2 = 2, ADD_Q3 = 3, ADD_Q4 = 4, ADD_PLAYER = 0;
    LinearLayout firstQLL, secondQLL, thirdQLL, fourthQLL, playersLL;
    LinearLayout.LayoutParams layoutParams;
    DatabaseReference gameInfoRef;
    String gameCode, homeName, homeColor, awayName, awayColor, currentPlayer;
    TextView gameHeader, homeOverallPoints, awayOverallPoints;
    Button statsDoneButton;
    int homeStats[] = {0, 0, 0, 0, 0, 0, 0}, awayStats[] = {0, 0, 0, 0, 0, 0, 0};
    int homeOverall = 0, awayOverall = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_stats);
        getActionBar().hide();

        gameCode = getIntent().getStringExtra(getString(R.string.intentGameID));

        statsDoneButton = (Button) findViewById(R.id.statsDoneButton);
        statsDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        firstQLL = (LinearLayout) findViewById(R.id.firstQLL);
        secondQLL = (LinearLayout) findViewById(R.id.secondQLL);
        thirdQLL = (LinearLayout) findViewById(R.id.thirdQLL);
        fourthQLL = (LinearLayout) findViewById(R.id.fourthQLL);
        playersLL = (LinearLayout) findViewById(R.id.playersLL);

        gameHeader = (TextView) findViewById(R.id.gameHeaderView);
        homeOverallPoints = (TextView) findViewById(R.id.homeOverallPoints);
        awayOverallPoints = (TextView) findViewById(R.id.awayOverallPoints);

        gameInfoRef = FirebaseDatabase.getInstance().getReference().child("games").child(gameCode);
        gameInfoRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                String branch = dataSnapshot.getKey();
                if(branch.equals("events")) {
                    for(DataSnapshot quarterSnapshots: dataSnapshot.getChildren()) {
                        String currentQuarter = quarterSnapshots.getKey();
                        for(int i = 0; i < ((ArrayList<String>) quarterSnapshots.getValue()).size(); i++) {
                            if(currentQuarter.equals("quarter_one")) {
                                generateUI("Event " + i + ": ", ((ArrayList<String>) quarterSnapshots.getValue()).get(i).toString(), ADD_Q1);
                            } else if(currentQuarter.equals("quarter_two")) {
                                generateUI("Event " + i + ": ", ((ArrayList<String>) quarterSnapshots.getValue()).get(i).toString(), ADD_Q2);
                            } else if(currentQuarter.equals("quarter_three")) {
                                generateUI("Event " + i + ": ", ((ArrayList<String>) quarterSnapshots.getValue()).get(i).toString(), ADD_Q3);
                            } else if(currentQuarter.equals("quarter_four")) {
                                generateUI("Event " + i + ": ", ((ArrayList<String>) quarterSnapshots.getValue()).get(i).toString(), ADD_Q4);
                            }
                        }
                    }
                } else if (branch.equals("home_properties")) {
                    for(DataSnapshot propertiesDS: dataSnapshot.getChildren()) {
                        if(propertiesDS.getKey().equals("color")) {
                            homeName = propertiesDS.getValue().toString();
                        } else if(propertiesDS.getKey().equals("name")) {
                            homeColor = propertiesDS.getValue().toString();
                        }
                    }
                } else if (branch.equals("away_properties")) {
                    for(DataSnapshot propertiesDS: dataSnapshot.getChildren()) {
                        if(propertiesDS.getKey().equals("color")) {
                            awayName = propertiesDS.getValue().toString();
                        } else if(propertiesDS.getKey().equals("name")) {
                            awayColor = propertiesDS.getValue().toString();
                        }
                    }
                } else if (branch.equals("name")) {
                    gameHeader.setText(dataSnapshot.getValue().toString() + " - " + gameCode);
                } else if(branch.equals("players")) {
                    //TODO -- color these based upon their teams
                    for(DataSnapshot teamPlayers: dataSnapshot.getChildren()) {
                        for(DataSnapshot player: teamPlayers.getChildren()) {
                            currentPlayer = player.getKey();
                            gameInfoRef.getParent().getParent().child("players").child(currentPlayer).child("player_name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot nameDataSnapshot) {
                                    Log.d("LOG", "FIREBASE: retrieved datasnapshot of value " + nameDataSnapshot.toString());
                                    try {
                                        generateUI(nameDataSnapshot.getValue().toString(), nameDataSnapshot.getRef().getParent().getKey(), ADD_PLAYER);
                                    } catch (NullPointerException e) {
                                        //do nothing basically
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError nameDatabaseError) {

                                }
                            });
                        }
                    }
                }
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
                Intent switchToViewActivity = new Intent(GameStatsActivity.this, PlayerInfoActivity.class);
                switchToViewActivity.putExtra(getString(R.string.intentPlayerID), intentPlayerID);
                startActivity(switchToViewActivity);
            }
        }
    };

    private void generateUI(String pName, String pID, int pInfoType) {
        TextView textView = new TextView(GameStatsActivity.this);
        View greyPadding = new View(GameStatsActivity.this);

        String actionStr = "";
        int teamColor = 0;

        if(pInfoType != ADD_PLAYER) {
            actionStr = extractAction(pID);
            teamColor = extractColor(pID);

            Log.d("LOG", "FIREBASE: Looking at "+pID);

            if(" ".equals(actionStr+" ")) {
                return;
            }

            textView.setBackgroundColor(teamColor);
            textView.setText(actionStr);
        }

        if(pInfoType == ADD_Q1) {
            firstQLL.addView(textView);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(20, 25, 20, 25);
            textView.setHapticFeedbackEnabled(true);
        } else if(pInfoType == ADD_Q2) {
            secondQLL.addView(textView);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(20, 25, 20, 25);
            textView.setHapticFeedbackEnabled(true);
        } else if(pInfoType == ADD_Q3) {
            thirdQLL.addView(textView);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(20, 25, 20, 25);
            textView.setHapticFeedbackEnabled(true);
        } else if(pInfoType == ADD_Q4) {
            fourthQLL.addView(textView);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(20, 25, 20, 25);
            textView.setHapticFeedbackEnabled(true);
        } else if(pInfoType == ADD_PLAYER) {
            playersLL.addView(textView);
            playersLL.addView(greyPadding);

            textView.setBackgroundColor(Color.parseColor("#"+getString(R.string.orange_hex)));
            textView.setMinHeight(40);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(20, 25, 20, 25);
            textView.setText(pName);
            textView.setHint(pID);
            textView.setHapticFeedbackEnabled(true);
            textView.setOnClickListener(playerClickListener);

            greyPadding.setLayoutParams(layoutParams);
            greyPadding.setBackgroundColor(Color.WHITE);
            greyPadding.setMinimumHeight(3);
        }
    }

    String extractAction(String pStr) {
        String action = "";
        boolean isHome = pStr.charAt(0) == 'H' ? true : false;
        char actionType = pStr.charAt(1);

        switch (actionType) {
            case 'A':
                if(isHome) {
                    homeStats[0]++;
                } else {
                    awayStats[0]++;
                }
                break;
            case '1':
                action = " 1 Pointer";
                if(isHome) {
                    homeOverall += 1;
                    homeStats[1]++;
                } else {
                    awayOverall += 1;
                    awayStats[1]++;
                }
                updateScores();
                break;

            case '2':
                action = " 2 Pointer";
                if(isHome) {
                    homeOverall += 2;
                    homeStats[2]++;
                } else {
                    awayOverall += 2;
                    awayStats[2]++;
                }
                updateScores();
                break;
            case '3':
                action = " 3 Pointer";
                if(isHome) {
                    homeOverall += 3;
                    homeStats[3]++;
                } else {
                    awayOverall += 3;
                    awayStats[3]++;
                }
                updateScores();
                break;
            case 'T':
                action = " Turnover";
                if(isHome) {
                    homeStats[4]++;
                } else {
                    awayStats[4]++;
                }
                break;

            case 'R':
                action = " Rebound";
                if(isHome) {
                    homeStats[5]++;
                } else {
                    awayStats[5]++;
                }
                break;
            case 'F':
                action = " Foul";
                if(isHome) {
                    homeStats[6]++;
                } else {
                    awayStats[6]++;
                }
                break;
        }

        if(isHome) {
            action = "Home"+action;
        } else {
            action = "Away"+action;
        }

        return action;
    }

    int extractColor(String pStr) {
        String colorChosen = pStr.charAt(0) == 'H' ? getString(R.string.red_hex) : getString(R.string.blue_hex);
        return Color.parseColor("#"+colorChosen);
    }

    void updateScores() {
        homeOverallPoints.setText(""+homeOverall);
        awayOverallPoints.setText(""+awayOverall);
    }
}
