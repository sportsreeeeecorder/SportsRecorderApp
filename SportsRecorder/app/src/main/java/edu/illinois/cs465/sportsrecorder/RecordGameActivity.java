package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class RecordGameActivity extends Activity {

    final String ADD_ASSIST = "A", ADD_1PT = "1", ADD_2PT = "2", ADD_3PT = "3", ADD_REBOUND = "R", ADD_TURNOVER = "T", ADD_FOUL = "F";
    DatabaseReference gameRef;

    LinearLayout.LayoutParams layoutParams;
    LinearLayout addPointsLL, scoreOptionsLL, homeChoices, awayChoices;

    Button awayOtherScore, homeOtherScore, addPointsButton, addOneBtn, addTwoBtn, addThreeBtn, addFoul, addTurnover, addRebound, addAssist, newQuarter;
    TextView gameInfoView;

    String events[] = {"", "", "", ""};
    HashMap<String, String> playerScores;
    int currQuarter = 0, homeOverall = 0, awayOverall = 0, updatedStat = 0;
    String currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_game);
        getActionBar().hide();

        gameRef = FirebaseDatabase.getInstance().getReference().child("games").child(UserInfo.gameCode);

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        playerScores = new HashMap<>();

        gameInfoView = (TextView) findViewById(R.id.scoreAndQuarterView);
        updateHeader();

        addPointsLL = (LinearLayout) findViewById(R.id.addPointsLL);
        scoreOptionsLL = (LinearLayout) findViewById(R.id.scoreOptionsLL);
        homeChoices = (LinearLayout) findViewById(R.id.homeChoices);
        awayChoices = (LinearLayout) findViewById(R.id.awayChoices);

        awayOtherScore = (Button) findViewById(R.id.awayOtherScore);
        homeOtherScore = (Button) findViewById(R.id.homeOtherScore);

        homeOtherScore.setOnClickListener(updateToPlayerClickListener);
        awayOtherScore.setOnClickListener(updateToPlayerClickListener);

        addPointsButton = (Button) findViewById(R.id.addPointsButton);
        addAssist = (Button) findViewById(R.id.addAssistsButton);
        addOneBtn = (Button) findViewById(R.id.addOneBtn);
        addTwoBtn = (Button) findViewById(R.id.addTwoBtn);
        addThreeBtn = (Button) findViewById(R.id.addThreeBtn);
        addFoul = (Button) findViewById(R.id.addFoulButton);
        addTurnover = (Button) findViewById(R.id.addTurnoverButton);
        addRebound = (Button) findViewById(R.id.addReboundButton);

        addPointsButton.setOnClickListener(showItemListener);
        addAssist.setOnClickListener(showItemListener);
        addOneBtn.setOnClickListener(showItemListener);
        addTwoBtn.setOnClickListener(showItemListener);
        addThreeBtn.setOnClickListener(showItemListener);
        addFoul.setOnClickListener(showItemListener);
        addTurnover.setOnClickListener(showItemListener);
        addRebound.setOnClickListener(showItemListener);

        newQuarter = (Button) findViewById(R.id.newQuarterButton);
        newQuarter.setOnClickListener(newQuarterListener);

        for(int i = 0; i < UserInfo.homeList.size(); i++){
            generateUI(UserInfo.homeList.get(i), true);
            playerScores.put(UserInfo.homeList.get(i), "0,0,0,0,0,0,0");
        }

        for(int i = 0; i < UserInfo.awayList.size(); i++){
            generateUI(UserInfo.awayList.get(i), false);
            playerScores.put(UserInfo.awayList.get(i), "0,0,0,0,0,0,0");
        }
    }

    View.OnClickListener newQuarterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            currQuarter++;
            if(currQuarter == 3) {
                ((Button) v).setText("End Game");
            } else if (currQuarter == 4) {
                Intent toPostGameStatsIntent = new Intent(RecordGameActivity.this, GameStatsActivity.class);
                toPostGameStatsIntent.putExtra(getString(R.string.intentGameID), UserInfo.gameCode);
                startActivity(toPostGameStatsIntent);
                finish();
            }

            addPointsLL.setVisibility(View.GONE);
            updateHeader();
        }
    };

    View.OnClickListener showItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof Button) {
                switch (v.getId()) {
                    case R.id.addPointsButton:
                        addPointsLL.setVisibility(View.VISIBLE);
                        return;
                    case R.id.addAssistsButton:
                        currentEvent = ADD_ASSIST;
                        break;
                    case R.id.addOneBtn:
                        currentEvent = ADD_1PT;
                        break;
                    case R.id.addTwoBtn:
                        currentEvent = ADD_2PT;
                        break;
                    case R.id.addThreeBtn:
                        currentEvent = ADD_3PT;
                        break;
                    case R.id.addReboundButton:
                        addPointsLL.setVisibility(View.GONE);
                        currentEvent = ADD_REBOUND;
                        break;
                    case R.id.addFoulButton:
                        addPointsLL.setVisibility(View.GONE);
                        currentEvent = ADD_FOUL;
                        break;
                    case R.id.addTurnoverButton:
                        addPointsLL.setVisibility(View.GONE);
                        currentEvent = ADD_TURNOVER;
                        break;
                    default:
                        addPointsLL.setVisibility(View.GONE);
                        return;
                }
                scoreOptionsLL.setVisibility(View.VISIBLE);
            }
        }
    };

    View.OnClickListener updateToPlayerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof Button) {
                String forUser = ((TextView) v).getHint().toString();
                if(forUser.equals("HomeOther")) {
                    updateDB(currentEvent, true);
                } else if (forUser.equals("AwayOther")) {
                    updateDB(currentEvent, false);
                } else if(UserInfo.homeList.contains(forUser)) {
                    updateDB(currentEvent, true, forUser);
                } else {
                    updateDB(currentEvent, false, forUser);
                }
                addPointsLL.setVisibility(View.GONE);
            }
        }
    };

    void updateHeader() {
        String quarterName;

        switch (currQuarter) {
            case 1: quarterName = "2nd Quarter";
                    break;
            case 2: quarterName = "3rd Quarter";
                    break;
            case 3: quarterName = "4th Quarter";
                    break;
            default: quarterName = "1st Quarter";
        }

        gameInfoView.setText("Home: " + homeOverall + " | " + quarterName + " | " + "Away: " + awayOverall);
    }

    void updateDB(String pEventToAdd, boolean pIsHome) {
        String eventValue = pIsHome ? "H" : "A";
        eventValue += pEventToAdd;

        String branchToAddTo;

        switch (currQuarter) {
            case 1: branchToAddTo = "quarter_two";
                break;
            case 2: branchToAddTo = "quarter_three";
                break;
            case 3: branchToAddTo = "quarter_four";
                break;
            default: branchToAddTo = "quarter_one";
        }

        if(currentEvent.equals(ADD_1PT)) {
            if(pIsHome) {
                homeOverall++;
            } else {
                awayOverall++;
            }
        } else if(currentEvent.equals(ADD_2PT)) {
            if(pIsHome) {
                homeOverall+=2;
            } else {
                awayOverall+=2;
            }
        } else if(currentEvent.equals(ADD_3PT)) {
            if (pIsHome) {
                homeOverall += 3;
            } else {
                awayOverall += 3;
            }
        }

        events[currQuarter] += events[currQuarter].length() == 0 ? eventValue : ","+eventValue;
        String eventSplit[] = events[currQuarter].split(",");
        gameRef.child("events").child(branchToAddTo).child((eventSplit.length - 1) + "").setValue(eventValue);

        updateHeader();
        scoreOptionsLL.setVisibility(View.GONE);
    }

    void updateDB(String pEventToAdd, boolean pIsHome, String pUser) {
        String eventValue = pIsHome ? "H" : "A";
        eventValue += pEventToAdd;

        String branchToAddTo;

        switch (currQuarter) {
            case 1: branchToAddTo = "quarter_two";
                break;
            case 2: branchToAddTo = "quarter_three";
                break;
            case 3: branchToAddTo = "quarter_four";
                break;
            default: branchToAddTo = "quarter_one";
        }

        events[currQuarter] += events[currQuarter].length() == 0 ? eventValue : ","+eventValue;
        String eventSplit[] = events[currQuarter].split(",");
        gameRef.child("events").child(branchToAddTo).child((eventSplit.length - 1) + "").setValue(eventValue);

        String[] playerScoresSplit = playerScores.get(pUser).split(",");
        Log.d("LOG", playerScoresSplit.toString());
        final int convertedPlayerScores[] = {Integer.valueOf(playerScoresSplit[0]), Integer.valueOf(playerScoresSplit[1]), Integer.valueOf(playerScoresSplit[2]), Integer.valueOf(playerScoresSplit[3]), Integer.valueOf(playerScoresSplit[4]), Integer.valueOf(playerScoresSplit[5]), Integer.valueOf(playerScoresSplit[6])};

        String homeOrAway = pIsHome ? "home": "away";
        updatedStat = 0;

        if(currentEvent.equals(ADD_ASSIST)) {
            convertedPlayerScores[0]++;
        } else if(currentEvent.equals(ADD_1PT)) {
            convertedPlayerScores[1]++;
            updatedStat = 1;
            if(pIsHome) {
                homeOverall++;
            } else {
                awayOverall++;
            }
        } else if(currentEvent.equals(ADD_2PT)) {
            convertedPlayerScores[2]++;
            updatedStat = 2;
            if(pIsHome) {
                homeOverall+=2;
            } else {
                awayOverall+=2;
            }
        } else if(currentEvent.equals(ADD_3PT)) {
            convertedPlayerScores[3]++;
            updatedStat = 3;
            if(pIsHome) {
                homeOverall+=3;
            } else {
                awayOverall+=3;
            }
        } else if(currentEvent.equals(ADD_TURNOVER)) {
            convertedPlayerScores[4]++;
            updatedStat = 4;
        } else if(currentEvent.equals(ADD_REBOUND)) {
            convertedPlayerScores[5]++;
            updatedStat = 5;
        } else {
            convertedPlayerScores[6]++;
            updatedStat = 6;
        }

        String reconvertedScores = "";

        for (int i = 0; i < 7; i++) {
            reconvertedScores += convertedPlayerScores[i]+"";
            if(i != 6) {
                reconvertedScores += ",";
            }
        }

        playerScores.put(pUser, reconvertedScores);

        for(int i = 0; i < 7; i++) {
            gameRef.child("players").child(homeOrAway).child(pUser).child(i + "").setValue(convertedPlayerScores[i]);
            gameRef.getParent().getParent().child("players").child(pUser).child("games_played").child(UserInfo.gameCode).child(i + "").setValue(convertedPlayerScores[i]);
        }

        final String copyOfUser = pUser;
        gameRef.getParent().getParent().child("players").child(pUser).child("career_stats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Long> careerStats = (ArrayList<Long>) dataSnapshot.getValue();
                careerStats.set(updatedStat, careerStats.get(updatedStat) + 1);
                for(int i = 0; i < 7; i++) {
                    gameRef.getParent().getParent().child("players").child(copyOfUser).child("career_stats").child("" + i).setValue(careerStats.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateHeader();
        scoreOptionsLL.setVisibility(View.GONE);
    }

    void generateUI(String pID, boolean pIsHome) {
        Button button = new Button(RecordGameActivity.this);

        if(pIsHome) {
            homeChoices.addView(button);
        } else {
            awayChoices.addView(button);
        }
        button.setLayoutParams(layoutParams);
        button.setPadding(20, 25, 20, 25);
        button.setText(UserInfo.codeNameLinkage.get(pID));
        button.setHint(pID);
        button.setHapticFeedbackEnabled(true);
        button.setOnClickListener(updateToPlayerClickListener);
    }
}
