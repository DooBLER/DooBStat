package net.doobler.doobstat.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.doobler.doobstat.DooBStat;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;


public class CleanPlayersTask extends BukkitRunnable {
	
	private DooBStat plugin;
	// jeśli is_working = true nie powinna być tworzona instancja tej klasy
	public static boolean is_working = false;
	// czy gracze do wywalenia mają być brani z plików dat czy z bazy
	private boolean from_dat = false;
	
	private List<String> source_list = new ArrayList<String>();
	

	public CleanPlayersTask(DooBStat plugin) {
		this(plugin, false);
	}
	public CleanPlayersTask(DooBStat plugin, boolean from_dat) {
		this.plugin = plugin;
		CleanPlayersTask.is_working = true;
		this.from_dat = from_dat;
		
		if(this.from_dat) {
			this.source_list = this.plugin.db.getAllNames();
		} else {
			this.source_list = this.plugin.db.getCleanNames();
		}
	}
	
	
	private List<String> getNamesFromDat() {
		List<String> player_names = new ArrayList<String>();
		
		Date curdate = new Date();
		long olderthan = curdate.getTime() - 
				(this.plugin.getConfig().getInt("clean.days")*24*3600*(long)1000);
		
		// zmiana listy na set
		Set<String> source_set = new HashSet<String>(this.source_list);
		// pobranie graczy offline
		OfflinePlayer[] offline_players = this.plugin.getServer().getOfflinePlayers();
		
		// przygotowanie listy graczy do wywalenia
		for (OfflinePlayer offline_pl : offline_players) {
			if(!source_set.contains(offline_pl.getName()) &&
					offline_pl.getLastPlayed() < olderthan) {
				player_names.add(offline_pl.getName());
			}
			
		}
		
		return player_names;
	}
	
	@Override
	public void run() {
		
		List<String> players_to_del;
		
		if(this.from_dat) {
			players_to_del = this.getNamesFromDat();
		} else {
			players_to_del = this.source_list;
		}

		
		// wyzerowanie liczników w klasie usuwającej
		DeletePlayerTask.delcount = 1;
		DeletePlayerTask.delmax = players_to_del.size();
		
		if(!players_to_del.isEmpty()) {
			int counter = 1;
			Iterator<String> pl_iter = players_to_del.iterator();
			while(pl_iter.hasNext()) {
				new DeletePlayerTask(this.plugin,
						pl_iter.next()).runTaskLater(this.plugin, (counter*20));
				counter += 1;
			}
		} else {
			CleanPlayersTask.is_working = false;
		}
	}

}
