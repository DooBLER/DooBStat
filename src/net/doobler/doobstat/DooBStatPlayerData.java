package net.doobler.doobstat;

import java.util.Date;

public class DooBStatPlayerData {
	private int playerid;
	private String playername;
	private Date logindate;
	
	
	DooBStatPlayerData(int id, String playername, Date thislogin) {
		this.playerid = id;
		this.playername = playername;
		this.logindate = thislogin;
	}
	
	public int getPlayerId() {
		return this.playerid;
	}
	public String getPlayerName() {
		return this.playername;
	}
	public Date getLoginDate() {
		return this.logindate;
	}
	
}
