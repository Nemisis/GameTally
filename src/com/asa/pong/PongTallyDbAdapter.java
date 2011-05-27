package com.asa.pong;

import android.content.ContentValues;
import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class PongTallyDbAdapter {
	private static final String DATABASE_NAME = "pongDatabase.db";
	private static final String DATABASE_TABLE = "namesTable";
	private static final int DATABASE_VERSION = 2;

	// Variable to hold the database instance
	private SQLiteDatabase db;
	// Context of the application using the database.
	private final Context context;
	// Database open/upgrade helper
	private pongDbHelper dbHelper;

	/*
	 * Column names: ID -> NAME -> Name of the player GAMES -> Number of games
	 * played WINS -> Number of games won
	 */
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_GAMES = "games";
	public static final String KEY_WINS = "wins";

	public static final int ID_COLUMN = 0;
	public static final int NAME_COLUMN = 1;
	public static final int GAMES_COLUMN = 2;
	public static final int WINS_COLUMN = 3;

	// TODO: Create public field for each column in your table.

	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null, " + KEY_GAMES + " INTEGER, " + KEY_WINS
			+ " integer);";

	/*
	 * private static final String DATABASE_CREATE = "create table " +
	 * DATABASE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " +
	 * KEY_NAME + " text not null);";
	 */

	public PongTallyDbAdapter(Context _context) {
		this.context = _context;
		// Will generally pass "this" in as the Context
		dbHelper = new pongDbHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	public void close() {
		db.close();
	}

	/*
	 * calls up an instance of pongDbHelper, which is our local implementation
	 * of the SQLiteOpenHelper class
	 */
	public void open() throws SQLiteException {
		try {
			db = dbHelper.getWritableDatabase(); // handles opening the db
		} catch (SQLiteException ex) {
			db = dbHelper.getReadableDatabase();
		}
	}

	// Insert player information
	public long insertPlayer(PongItem pongData) {
		// Create a new row for the newly added player
		ContentValues newTaskValues = new ContentValues();
		// Assign values for each row.
		newTaskValues.put(KEY_NAME, pongData.getName());
		newTaskValues.put(KEY_GAMES, pongData.getGamesPlayed());
		newTaskValues.put(KEY_WINS, pongData.getGamesWon());

		return db.insert(DATABASE_TABLE, null, newTaskValues); // Returns the
																// row _id value
																// of the newly
																// created db
																// object
	}

	public boolean removeAll() {
		return db.delete(DATABASE_TABLE, null, null) > 0;
	}

	// remove player information
	public boolean removePlayer(long _rowIndex) {
		return db.delete(DATABASE_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;
	}

	// Update player information
	public boolean updateName(long rowIndex, String name) {
		ContentValues newValue = new ContentValues();
		newValue.put(KEY_NAME, name);
		return db.update(DATABASE_TABLE, newValue, KEY_ID + "=" + rowIndex,
				null) > 0;
	}

	// Returns all database objects
	public Cursor getAllEntriesCursor() {
		return db.query(DATABASE_TABLE, new String[] { KEY_ID, KEY_NAME,
				KEY_GAMES }, null, null, null, null, null);
	}

	public Cursor setCursorToPongItem(long rowIndex) throws SQLException {
		Cursor result = db.query(true, DATABASE_TABLE, new String[] { KEY_ID,
				KEY_NAME }, KEY_ID + "=" + rowIndex, null, null, null, null,
				null);

		if ((result.getCount() == 0) || !result.moveToFirst()) {
			throw new SQLException("No items found for row: " + rowIndex);
		}
		return result;
	}

	public String getName(long rowIndex) throws SQLException {
		Cursor cursor = db.query(true, DATABASE_TABLE, new String[] { KEY_ID,
				KEY_NAME }, KEY_ID + "=" + rowIndex, null, null, null, null,
				null);
		if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
			throw new SQLException("No items found for row: " + rowIndex);
		}
		return cursor.getString(NAME_COLUMN);
	}

	public Cursor getPongItem(long rowIndex) throws SQLException {
		// TODO: Return a cursor to a row from the database and use the values
		// to populate an instance of MyObject
		Cursor cursor = db.query(true, DATABASE_TABLE, new String[] { KEY_ID,
				KEY_NAME }, KEY_ID + "=" + rowIndex, null, null, null, null,
				null);
		if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
			throw new SQLException("No items found for row: " + rowIndex);
		}
		// String name = cursor.getString(NAME_COLUMN);
		// int gamesPlayed = cursor.getInt(GAMES_COLUMN);

		// PongItem objectInstance = new PongItem(name, gamesPlayed, 0);
		return cursor;
	}

	private static class pongDbHelper extends SQLiteOpenHelper {
		public pongDbHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// Creates the database when no database exists in the disk
		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE);
		}

		// Called when there is a database version mismatch, which means the
		// version needs to be upgraded.
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			// Log the version upgrade.
			Log.w("TaskDBAdapter", "Upgrading from version" + _oldVersion
					+ " to " + _newVersion
					+ ", which will destroy ALL old data.");
			// Upgrade the existing database to conform to the new version.
			// Multiple previous versions can be handled by comparing
			// _oldVersion and _newVersion values.

			// The easiest way to do this is to drop old table and create a new
			// one
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(_db);
		}
	}
}
