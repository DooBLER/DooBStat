package net.doobler.doobstat.utils;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import net.doobler.doobstat.DooBStat;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class DeletePlayerTask extends BukkitRunnable {

	private static DooBStat plugin;
	private String player_name;
	private List<String> cmds;
	
	public static int delcount = 0;
	public static int delmax = 0;
	
	public DeletePlayerTask(DooBStat plugin, String player) {
		DeletePlayerTask.plugin = plugin;
		this.player_name = player;
		this.cmds = DeletePlayerTask.plugin.getConfig().getStringList("clean.commands");
	}
	
	@Override
	public void run() {
		
		long time = System.nanoTime();
		
		Server serv = DeletePlayerTask.plugin.getServer();
		
		Player player = serv.getPlayer(this.player_name);
		
		// sprawdza czy gracz jest offline czyli = null lub nie jest online
		if(player == null || !player.isOnline()) {
			List<World> worlds = serv.getWorlds();
			
			Iterator<World> witer = worlds.iterator();
			Iterator<String> cmditer = cmds.iterator();
			
			// odpalenie dodatkowych komend
			while(cmditer.hasNext()) {
				String cmd = cmditer.next().replaceAll("\\$player_name\\$", this.player_name);
				serv.dispatchCommand(serv.getConsoleSender(), cmd);
			}
			
			// wywalenie plików .dat danego gracza ze wszystkich światów
			while(witer.hasNext()) {
				World w = witer.next();
				String path = w.getWorldFolder() + File.separator +
						"players" + File.separator + this.player_name + ".dat";
				
				File datFile = new File(path);
				datFile.delete();
			}
			
			// wywalenie z bazy DooBStat
			DeletePlayerTask.plugin.db.removePlayer(this.player_name);
			
			if(DeletePlayerTask.plugin.getConfig().getBoolean("debug")) {
				plugin.getLogger().info("Cleaning ("+ DeletePlayerTask.delcount +
						"/" + DeletePlayerTask.delmax +
						"), player: " + this.player_name +
						" deleted. in: " + (System.nanoTime() - time));
			} else {
				plugin.getLogger().info("Cleaning ("+ DeletePlayerTask.delcount +
						"/" + DeletePlayerTask.delmax +
						"), player: " + this.player_name + " deleted.");
			}
			
		} else {
			plugin.getLogger().info("Cleaning ("+ DeletePlayerTask.delcount +
					"/" + DeletePlayerTask.delmax +
					"), player: " + this.player_name +
					" not deleted, status: online");
		}
		
		DeletePlayerTask.delcount += 1;

		if(DeletePlayerTask.delmax > 0 && DeletePlayerTask.delmax == DeletePlayerTask.delcount) {
			CleanPlayersTask.is_working = false;
		}
	}

}
