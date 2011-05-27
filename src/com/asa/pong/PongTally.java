package com.asa.pong;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class PongTally extends ListActivity {
	// Holds the pong items list
	private ArrayList<PongItem> pongItems;
	public ArrayList<Integer> teamOne = new ArrayList<Integer>();
	public ArrayList<Integer> teamTwo = new ArrayList<Integer>();
	private ArrayList<Integer> tempList = new ArrayList<Integer>();
	private boolean[] checkedList1;
	private boolean[] checkedList2;
	private PongItemAdapter aa;
	private ListView myListView;
	private EditText myEditText;
	AlertDialog.Builder teamOneDialog;// = new AlertDialog.Builder(this);
	private AlertDialog.Builder teamTwoDialog;// = new
												// AlertDialog.Builder(this);

	// public static final int ADD_PLAYER_ID = Menu.FIRST;
	public static final int DELETE_LIST_ID = Menu.FIRST;
	private static final int DELETE_PLAYER_ID = Menu.FIRST + 1;
	private static final int EDIT_PLAYER_ID = Menu.FIRST + 2;
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int ACTIVITY_GAME = 2;
	private static final int DIALOG_REMOVE_LIST = 0;
	private MenuItem mItem;

	/**
	 * false = First time addition. true = editing already existing name
	 */
	public static boolean editType = false;

	PongTallyDbAdapter mDbHelper;
	Cursor pongTallyCursor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.players_list);

		pongItems = new ArrayList<PongItem>();
		aa = new PongItemAdapter(this, R.layout.players_list, pongItems);

		Button addPlayer = (Button) findViewById(R.id.addPlayers);
		Button startGame = (Button) findViewById(R.id.startGame);

		// Set what the button does when a the "Add Player" button has been
		// selected
		addPlayer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				addPlayerButtonClicked();
			}
		});

		startGame.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startGameButtonClicked();
			}
		});

		registerForContextMenu(getListView());
		// restoreUIState();
		// Open the database
		mDbHelper = new PongTallyDbAdapter(this);
		mDbHelper.open();
		populateList();
	}

	/**
	 * This method is called when the activity begins. It gets ALL the data from
	 * the database and then calls updateArray which updates the ArrayList
	 * pongItems with the information from the database. This will accurately
	 * display the list every time the Activity begins.
	 */
	private void populateList() {
		// Get all the PongTally items from the database
		// pongTallyCursor = mDbHelper.getAllEntriesCursor();
		// startManagingCursor(pongTallyCursor); //Allows application to manage
		// cursor
		// updateArray();
		Cursor notesCursor = mDbHelper.getAllEntriesCursor();
		startManagingCursor(notesCursor);

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { PongTallyDbAdapter.KEY_NAME,
				PongTallyDbAdapter.KEY_GAMES };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.names, R.id.games };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.win_games, notesCursor, from, to);
		setListAdapter(notes);
	}

	/**
	 * @see populateList
	 */
	public void updateArray() {
		pongTallyCursor.requery(); // Ensures that the cursor is fully up to
									// date

		pongItems.clear();
		if (pongTallyCursor.moveToFirst()) {
			do {
				String name = pongTallyCursor.getString(mDbHelper.NAME_COLUMN);
				int gamesPlayed = pongTallyCursor
						.getInt(mDbHelper.GAMES_COLUMN);
				int gamesWon = pongTallyCursor.getInt(mDbHelper.WINS_COLUMN);
				PongItem newItem = new PongItem(name, gamesPlayed, gamesWon);
				pongItems.add(0, newItem);

			} while (pongTallyCursor.moveToNext());
		}
		aa.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, DELETE_LIST_ID, 0, R.string.deleteList);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_LIST_ID:
			editType = false;
			AlertDialog.Builder deletePlayerDialog = new AlertDialog.Builder(
					PongTally.this);
			deletePlayerDialog.setMessage(R.string.deleteListMessage);
			deletePlayerDialog.setCancelable(true).setTitle(
					R.string.deleteListTitle);
			deletePlayerDialog
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									mDbHelper.removeAll();
									populateList();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).show();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_PLAYER_ID, 0, R.string.deletePlayerTitle);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		mItem = item;
		switch (item.getItemId()) {
		case DELETE_PLAYER_ID:
			AlertDialog.Builder deletePlayerDialog = new AlertDialog.Builder(
					PongTally.this);
			deletePlayerDialog.setMessage(R.string.deletePlayerMessage);
			deletePlayerDialog.setCancelable(true).setTitle(
					R.string.deletePlayerTitle);
			deletePlayerDialog
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									AdapterContextMenuInfo info = (AdapterContextMenuInfo) mItem
											.getMenuInfo();
									mDbHelper.removePlayer(info.id);
									populateList();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).show();
			return true;
		case EDIT_PLAYER_ID:
			Intent i = new Intent(this, PlayerEdit.class);
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			i.putExtra(PongTallyDbAdapter.KEY_ID, info.id);
			// i.putExtras(infoBundle);
			editType = true;
			startActivityForResult(i, ACTIVITY_EDIT);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * Tells Android what to do when an item is clicked. In this case, we will
	 * want to be able to edit the name of a player.
	 * 
	 * @param l
	 *            the ListView object it was invoked from
	 * @param v
	 *            he View inside the ListView that was clicked on
	 * @param position
	 *            the position in the list that was clicked
	 * @param id
	 *            the mRowId of the item that was clicked
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, PlayerEdit.class);
		i.putExtra(PongTallyDbAdapter.KEY_ID, id);
		// i.putExtras(infoBundle);
		editType = true;
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	/**
	 * This is called when an activity returns results (a bundle typically).
	 * 
	 * @param requestCode
	 *            the original request code specified in the Intent invocation
	 *            (either ACTIVITY_CREATE or ACTIVITY_EDIT for us)
	 * @param resultCode
	 *            the result (or error code) of the call, this should be zero if
	 *            everything was OK
	 * @param inten
	 *            this is an Intent created by the Activity returning results.
	 *            It can be used to return data in the Intent "extras."
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		populateList();

	}

	@Override
	public void onDestroy() {
		mDbHelper.close();
		super.onDestroy();
	}

	private void addPlayerButtonClicked() {
		EditText addPlayerBox = (EditText) findViewById(R.id.playerNameBox);
		Button addPlayerButton = (Button) findViewById(R.id.confirm);
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		addPlayerButton.setVisibility(View.VISIBLE);
		addPlayerBox.setVisibility(View.VISIBLE);
		cancelButton.setVisibility(View.VISIBLE);

		addPlayerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				EditText addPlayerBox = (EditText) findViewById(R.id.playerNameBox);
				Button addPlayerButton = (Button) findViewById(R.id.confirm);
				Button cancelButton = (Button) findViewById(R.id.cancelButton);
				// Adding the new player to the ArrayAdapter
				PongItem newItem = new PongItem(addPlayerBox.getText()
						.toString());
				mDbHelper.insertPlayer(newItem);
				// updateArray();
				populateList();
				aa.notifyDataSetChanged();

				addPlayerButton.setVisibility(View.GONE);
				addPlayerBox.setVisibility(View.GONE);
				cancelButton.setVisibility(View.GONE);
				addPlayerBox.setText("");
				addPlayerBox.setHint(R.string.editPlayerHint);

				setResult(RESULT_OK);
			}

		});
		cancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				EditText addPlayerBox = (EditText) findViewById(R.id.playerNameBox);
				Button addPlayerButton = (Button) findViewById(R.id.confirm);
				Button cancelButton = (Button) findViewById(R.id.cancelButton);
				addPlayerButton.setVisibility(View.GONE);
				addPlayerBox.setVisibility(View.GONE);
				cancelButton.setVisibility(View.GONE);
				addPlayerBox.setText("");
				addPlayerBox.setHint(R.string.editPlayerHint);

				setResult(RESULT_OK);
			}
		});
	}

	// Use a cursor to extract all the members of the database and populate the
	// list like that

	private void startGameButtonClicked() {
		Cursor cursor = mDbHelper.getAllEntriesCursor();
		if(cursor.getCount() > 0){
			createTeamOneDialog();
		}else{
			Context context = getApplicationContext();
			String toastText = "There are no players listed. You must add players before starting a game.";
			Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
			toast.show();
		}
		// String[] players = new String[]{PongTallyDbAdapter.KEY_NAME,
		// PongTallyDbAdapter.KEY_GAMES};
	}

	private void createTeamOneDialog() {
		teamOneDialog = new AlertDialog.Builder(this);
		teamOneDialog.setTitle(R.string.chooseTeamOneTitle);
		Cursor dbEntries = mDbHelper.getAllEntriesCursor();
		ArrayList<String> namesArrayList = new ArrayList<String>();
		ArrayList<Integer> idArrayList = new ArrayList<Integer>();
		if (dbEntries.moveToFirst()) {
			while (dbEntries.moveToNext()) {
				int id = dbEntries.getInt(PongTallyDbAdapter.ID_COLUMN);
				String name = mDbHelper.getName(id);
				namesArrayList.add(name);
				idArrayList.add(id);
			}
		}
		String[] namesList = (String[]) namesArrayList
				.toArray(new String[namesArrayList.size()]);
		checkedList1 = new boolean[namesArrayList.size()];
		for (int pos = 0; pos < namesArrayList.size(); pos++) { // set all
																// values to
																// false
			checkedList1[pos] = false;
		}
		// Creates the dialog for choosing team 1
		teamOneDialog.setMultiChoiceItems(namesList, checkedList1,
				new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface arg0, int arg1,
							boolean arg2) {

					}
				});
		// Sets what the positive/negative button for the team one dialog box
		// does
		teamOneDialog.setPositiveButton("Done",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						createTeamTwoDialog();
						dialog.cancel();
					}
				}).setNegativeButton(R.string.cancelButton,
				new DialogInterface.OnClickListener() {
					// When pushed, the checked list is cleared.
					public void onClick(DialogInterface dialog, int id) {
						for (int i = 0; i < checkedList1.length; i++) {
							checkedList1[i] = false;
						}
						dialog.dismiss();
					}
				});
		tempList = idArrayList;
		teamOneDialog.show();
	}

	private void createTeamTwoDialog() {
		teamTwoDialog = new AlertDialog.Builder(this);
		teamTwoDialog.setTitle("Choose Team Two");
		Cursor dbEntries = mDbHelper.getAllEntriesCursor();
		ArrayList<String> namesArrayList = new ArrayList<String>();
		ArrayList<Integer> idArrayList = new ArrayList<Integer>();
		// Adds the elements of the temporary list made above to the teamOne
		// list to store who is on team one.
		for (int i = 0; i < checkedList1.length; i++) {
			if (checkedList1[i]) {
				teamOne.add(tempList.get(i));
			}
		}
		// Iterates through the cursor, extracting the elements
		if (dbEntries.moveToFirst()) {
			while (dbEntries.moveToNext()) {
				int id = dbEntries.getInt(PongTallyDbAdapter.ID_COLUMN);
				String name = mDbHelper.getName(id);
				if (!teamOne.contains(id)) {
					namesArrayList.add(name);
					idArrayList.add(id);
				} else {
					// System.out.println("~!" + name);
				}
			}
		}
		String[] namesList = (String[]) namesArrayList
				.toArray(new String[namesArrayList.size()]);
		checkedList2 = new boolean[namesArrayList.size()];
		for (int pos = 0; pos < namesArrayList.size(); pos++) { // set all
																// values to
																// false
			checkedList2[pos] = false;
		}
		// Creates the dialog for choosing team 1
		teamTwoDialog.setMultiChoiceItems(namesList, checkedList2,
				new DialogInterface.OnMultiChoiceClickListener() {

					public void onClick(DialogInterface arg0, int arg1,
							boolean arg2) {
						// do nothing
					}
				});
		// Sets what the positive/negative button for the team one dialog
		// box does
		teamTwoDialog.setPositiveButton("Done",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						setTeamTwo();
						Intent i = new Intent(PongTally.this,
								GameActivity.class);
						// i.putExtra(PongTallyDbAdapter.KEY_ID, info.id);
						Bundle bundle = new Bundle();
						bundle.putIntegerArrayList(GameActivity.TEAM_ONE, teamOne);
						bundle.putIntegerArrayList(GameActivity.TEAM_TWO, teamTwo);
						i.putExtra(GameActivity.TEAM_INFO, bundle);
						//startActivityForResult(i, ACTIVITY_GAME);
						dialog.cancel();
					}
				}).setNegativeButton(R.string.cancelButton,
				new DialogInterface.OnClickListener() {
					// When Canceled, both checkedlists are cleared as is
					// the elements in teamOne because the user is going to
					// start over.
					public void onClick(DialogInterface dialog, int id) {
						teamOne.clear();
						for (int i = 0; i < checkedList1.length; i++) {
							checkedList1[i] = false;
						}
						for (int i = 0; i < checkedList2.length; i++) {
							checkedList2[i] = false;
						}
						dialog.dismiss();
					}
				});
		tempList = idArrayList;
		teamTwoDialog.show();
	}

	private void setTeamTwo() {

		for (int i = 0; i < checkedList2.length; i++) {
			if (checkedList2[i]) {
				teamTwo.add(tempList.get(i));
			}
		}
		for (int i = 0; i < teamOne.size(); i++) {
			System.out.println("~!Team Two Player: " + teamTwo.get(i));
		}
	}
}