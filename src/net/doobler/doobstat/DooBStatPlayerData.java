package net.doobler.doobstat;

import java.util.Date;

public class DooBStatPlayerData {
	private int playerid;
	private String playername;
	private Date logindate;
	
	private int stat_bed_enter = 0;
	private int stat_fish = 0;
	
	private int block_place = 0;
	private int block_break = 0;
	
	private int death_count = 0;
	
	private int pvp_kills = 0;
	private int pvp_deaths = 0;
	private String pvp_killer = "";
	private String pvp_victim = "";
	
	private double[] dist = new double[7];
	
	public static final char FOOT = 0; // 0 - foot
	public static final char FLY  = 1; // 1 - fly
	public static final char SWIM = 2; // 2 - swim
	public static final char PIG  = 3; // 3 - pig
	public static final char CART = 4; // 4 - cart
	public static final char BOAT = 5; // 5 - boat
	public static final char HORSE = 6; // 5 - horse
	
	public DooBStatPlayerData(int id, String playername, Date thislogin) {
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
	
	/**
	 * Block Place
	 */
	public void addBlockPlace() {
		this.block_place += 1;
	}
	public int getBlockPlace() {
		return this.block_place;
	}
	
	/**
	 * Block Break
	 */
	public void addBlockBreak() {
		this.block_break += 1;
	}
	public int getBlockBreak() {
		return this.block_break;
	}
	
	/**
	 * Death count
	 */
	public void addDeath() {
		this.death_count += 1;
	}
	public int getDeath() {
		return this.death_count;
	}
	
	/**
	 * PVP Kill
	 */
	public void addPvpKill(String victim) {
		this.pvp_kills += 1;
		this.pvp_victim = victim;
	}
	public int getPvpKill() {
		return this.pvp_kills;
	}
	public String getPvpVictim() {
		return this.pvp_victim;
	}
	
	/**
	 * PVP Death
	 */
	public void addPvpDeath(String killer) {
		this.pvp_deaths += 1;
		this.pvp_killer = killer;
	}
	public int getPvpDeath() {
		return this.pvp_deaths;
	}
	public String getPvpKiller() {
		return this.pvp_killer;
	}
	
	
	
}
