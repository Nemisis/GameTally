package com.asa.pong;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PlayerEdit extends Activity {

	private PongTallyDbAdapter mDbHelper;
	EditText mNameText;
	Long mRowId;
	boolean firstTime = true;
	private ArrayList<PongItem> pongItems;
	private PongItemAdapter aa;
	Cursor pongTallyCursor;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_edit);
		setTitle(R.string.editPlayerName);
		mNameText = (EditText) findViewById(R.id.playerName);
		Button confirmButton = (Button) findViewById(R.id.confirm);
		Button cancelButton = (Button) findViewById(R.id.editCancelButton);
		
		mDbHelper = new PongTallyDbAdapter(this);
	    mDbHelper.open();
		
	    mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(PongTallyDbAdapter.KEY_ID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(PongTallyDbAdapter.KEY_ID)
                                    : null;
        }
		
        populateFields();
		confirmButton.setOnClickListener(new View.OnClickListener() {

		    public void onClick(View view) {
		        
		    	setResult(RESULT_OK);
                finish();
		    }

		});
		
		cancelButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view) {
				setResult(RESULT_CANCELED, null);
				finish();
			}
		});
		
	}
	
	private void addToDb(){
		PongItem newItem = new PongItem(mNameText.getText().toString());
		newItem.setGamesPlayed(0);
		newItem.setGamesWon(0);
		mDbHelper.insertPlayer(newItem);
		aa.notifyDataSetChanged();	
		
	}
	
	private void populateFields() {
	    if (mRowId != null) {
	    	Cursor note = mDbHelper.getPongItem(mRowId);
	        mNameText.setText(note.getString(
	                    note.getColumnIndexOrThrow(PongTallyDbAdapter.KEY_NAME)));
	    }
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(PongTallyDbAdapter.KEY_ID, mRowId);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private void saveState() {
        String name = mNameText.getText().toString();
        PongItem pongItem = new PongItem(name);
        if (mRowId == null) {
            long id = mDbHelper.insertPlayer(pongItem);
            if (id > 0) {
                mRowId = id;
            }
        } else {
        	mDbHelper.updateName(mRowId, name);
        }
    } 
}
