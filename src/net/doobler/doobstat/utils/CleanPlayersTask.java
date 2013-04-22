package net.doobler.doobstat.utils;

import java.util.Iterator;
import java.util.List;

import net.doobler.doobstat.DooBStat;

import org.bukkit.scheduler.BukkitRunnable;


public class CleanPlayersTask extends BukkitRunnable {
	
	private DooBStat plugin;
	// jeśli is_working = true nie powinna być tworzona instancja tej klasy
	public static boolean is_working = false;

	public CleanPlayersTask(DooBStat plugin) {
		this.plugin = plugin;
		CleanPlayersTask.is_working = true;
	}
	
	@Override
	public void run() {
		List<String> players_to_del = plugin.db.getCleanNames();
		
		// wyzerowanie liczników w klasie uduwającej
		DeletePlayerTask.delcount = 1;
		DeletePlayerTask.delmax = players_to_del.size();
		
		if(!players_to_del.isEmpty()) {
			int counter = 1;
			Iterator<String> pl_iter = players_to_del.iterator();
			while(pl_iter.hasNext()) {
				new DeletePlayerTask(
						this.plugin, pl_iter.next()).runTaskLater(this.plugin, (counter*20));
				counter += 1;
			}
		} else {
			CleanPlayersTask.is_working = false;
		}
	}

}
