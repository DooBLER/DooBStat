package net.doobler.doobstat.listeners;

import net.doobler.doobstat.DooBStat;
import net.doobler.doobstat.DooBStatPlayerData;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;


public class BlockListener implements Listener {
	
	public DooBStat plugin;
	
	public BlockListener(DooBStat plugin) {
	    this.plugin = plugin;
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		DooBStatPlayerData playerData = plugin.playerslist.get(event.getPlayer().getName().toLowerCase());
		
		playerData.addBlockBreak();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		DooBStatPlayerData playerData = plugin.playerslist.get(event.getPlayer().getName().toLowerCase());
		
		playerData.addBlockPlace();
	}
	
	
}
