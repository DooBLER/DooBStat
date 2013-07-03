package net.doobler.doobstat;

import java.util.Date;

public class DooBStatPlayerData {
	private int playerid;
	private String playername;
	private Date logindate;
	
	private int stat_bed_enter = 0;
	private int stat_fish = 0;
	
	private double[] dist = new double[7];
	public static final char FOOT = 0; // 0 - foot
	public static final char FLY  = 1; // 1 - fly
	public static final char SWIM = 2; // 2 - swim
	public static final char PIG  = 3; // 3 - pig
	public static final char CART = 4; // 4 - cart
	public static final char BOAT = 5; // 5 - boat
	public static final char HORSE = 6; // 5 - horse
	
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
	
	public void addDist(char pos, double dst) {
		this.dist[pos] += dst;
	}
	public double getDist(char pos) {
		return this.dist[pos];
	}
	
	public void addBedEnter() {
		this.stat_bed_enter += 1;
	}
	public int getBedEnter() {
		return this.stat_bed_enter;
	}
	
	public void addFish() {
		this.stat_fish += 1;
	}
	public int getFish() {
		return this.stat_fish;
	}
}
