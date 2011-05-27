package com.asa.pong;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class GameActivity extends Activity {
    /** Called when the activity is first created. */
	public static final String TEAM_ONE = "one";
	public static final String TEAM_TWO = "two";
	public static final String TEAM_INFO = "teams";
	PongTallyDbAdapter mDbHelper;
    Button teamOneButton = (Button) findViewById(R.id.teamOneButton);
    Button teamTwoButton = (Button) findViewById(R.id.teamTwoButton);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
		mDbHelper = new PongTallyDbAdapter(this);
		mDbHelper.open();
        teamOneButton = (Button) findViewById(R.id.teamOneButton);
        teamTwoButton = (Button) findViewById(R.id.teamTwoButton);
        
        Bundle bundle = getIntent().getBundleExtra(TEAM_INFO);
        ArrayList<Integer> teamOneId = bundle.getIntegerArrayList(TEAM_ONE);//new ArrayList<Integer>();
        ArrayList<Integer> teamTwoId = bundle.getIntegerArrayList(TEAM_TWO);//new ArrayList<Integer>();
        
        StringBuilder teamOne = new StringBuilder();
        StringBuilder teamTwo = new StringBuilder();
        for(int i = 0; i < teamOneId.size(); i++){
        	String name1 = mDbHelper.getName(teamOneId.get(i));
        	String name2 = mDbHelper.getName(teamTwoId.get(i));
        	teamOne.append(name1);
        	teamTwo.append(name2);
        	if(i != teamOneId.size() - 1){
        		teamOne.append(" & ");
        	}
        	if(i != teamTwoId.size() - 1){
        		teamTwo.append(" & ");
        	}
        }
        teamOneButton.setText(teamOne);
        teamTwoButton.setText(teamTwo);
    }
    
    @Override
    public void onDestroy(){
    	teamOneButton = (Button) findViewById(R.id.teamOneButton);
        teamTwoButton = (Button) findViewById(R.id.teamTwoButton);
    	teamOneButton.setText("");
    	teamTwoButton.setText("");
    	super.onDestroy();
    	
    }
}