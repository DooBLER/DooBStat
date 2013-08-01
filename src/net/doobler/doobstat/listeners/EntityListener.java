package net.doobler.doobstat.listeners;

import net.doobler.doobstat.DooBStat;
import net.doobler.doobstat.DooBStatPlayerData;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

//import org.bukkit.event.entity.EntityDamageByEntityEvent;
//import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;



public class EntityListener implements Listener {
	
	public DooBStat plugin;
	
	public EntityListener(DooBStat plugin) {
	    this.plugin = plugin;
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {
		
		LivingEntity victim = event.getEntity();
		
		if(victim instanceof Player) {
			
			DooBStatPlayerData playerVictimData = plugin.playerslist.get(((Player)victim).getName().toLowerCase());
			playerVictimData.addDeath();
			
			Player killer = victim.getKiller();
			
			// jeśli istnieje killer to jest to PVP
			if(killer != null) {
				DooBStatPlayerData playerKillerData = plugin.playerslist.get(killer.getName().toLowerCase());
			
				playerVictimData.addPvpDeath(playerKillerData.getPlayerName());
				playerKillerData.addPvpKill(playerVictimData.getPlayerName());
			
				return;
			}
// TODO
//			EntityDamageEvent cause = victim.getLastDamageCause();
//			
//			if(cause instanceof EntityDamageByEntityEvent)
//		    {
//
//		        EntityDamageByEntityEvent even = (EntityDamageByEntityEvent)cause;
//		        
//		        
//		        
//		        this.plugin.getLogger().info("damager: " + even.getDamager().getType().getName());
//
//		        //get killer, damagecauses, anything.
//
//		    }
			
			
		}

	}
	
	
	
}
