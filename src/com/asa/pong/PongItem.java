package com.asa.pong;

public class PongItem {

	String name;
	int gamesPlayed = 0;
	int gamesWon = 0;
	
	public PongItem(String _name, int gP, int gW){
		name = _name;
		gamesPlayed = gP;
		gamesWon = gW;
	}
	
	public PongItem(String _name){
		name = _name;
		gamesPlayed = 0;
		gamesWon = 0;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String _name){
		name = _name;
	}
	
	public int getGamesPlayed(){
		return gamesPlayed;
	}
	
	public void setGamesPlayed(int gp){
		gamesPlayed = gp;
	}
	
	public int getGamesWon(){
		return gamesWon;	
	}
	
	public void setGamesWon(int gw){
		gamesWon = gw;
	}
	
	public String toStringGP(){
		return Integer.toString(gamesPlayed);
	}
	
	public String toStringGW(){
		return Integer.toString(gamesWon);
	}
}
