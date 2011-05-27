package com.asa.pong;

import android.content.Context;
import java.util.*;
import android.view.*;
import android.widget.*;

public class PongItemAdapter extends ArrayAdapter<PongItem>{

	int resource;
	
	public PongItemAdapter(Context _context, int _resource, List<PongItem> _items){
		super(_context, _resource, _items);
		resource = _resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
		LinearLayout tallyView;
		
		PongItem item = getItem(position);
		
		String name = item.getName();
		int gamesPlayed = item.getGamesPlayed();
		
		if(convertView == null){
			tallyView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, tallyView, true);
		}else{
			tallyView = (LinearLayout) convertView;
		}
		TextView gamesView = (TextView) row.findViewById(R.id.games);
		TextView nameView = (TextView) row.findViewById(R.id.names);
		gamesView.setText(gamesPlayed);
		nameView.setText(name);
		
		/**
		 * LinearLayout tallyView;
		
		PongItem item = getItem(position);
		
		String name = item.getName();
		int gamesPlayed = item.getGamesPlayed();
		int gamesWon = item.getGamesWon();
		
		if(convertView == null){
			tallyView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, tallyView, true);
		}else{
			tallyView = (LinearLayout) convertView;
		}
		 */
		
	/*	TextView gamesView = (TextView) tallyView.findViewById(R.id.rowGames);
		TextView nameView = (TextView) tallyView.findViewById(R.id.rowNames);
		
		gamesView.setText(gamesWon + "/" + gamesPlayed);
		nameView.setText(name);*/
		
		return tallyView;
	}
	
	
}
