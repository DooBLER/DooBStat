package net.doobler.doobstat.listeners;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import net.doobler.doobstat.DooBStat;
import net.doobler.doobstat.DooBStatPlayerData;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerListener implements Listener {
	
	public DooBStat plugin;
	
	public PlayerListener(DooBStat plugin) {
	    this.plugin = plugin;
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		
		Date curdate = new Date();
		Timestamp curtimestamp = new Timestamp(curdate.getTime());
		
		PreparedStatement prest = this.plugin.db.getPreparedStatement("getPlayerByName");
		
		ResultSet res = null;
		
		try {
			prest.setString(1, event.getPlayer().getName());
			res = prest.executeQuery();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		

		try {
			// jeśli istnieje następny wiersz to dane zostały pobrane
    		if(res.next()) {
    			
    			PreparedStatement prest2 = this.plugin.db.getPreparedStatement("updatePlayerJoin");
    			
    			prest2.setString(1, event.getPlayer().getAddress().getAddress().getHostAddress());
    			prest2.setString(2, res.getString("this_login"));
    			prest2.setTimestamp(3, curtimestamp);
    			prest2.setInt(4, res.getInt("id"));
    			prest2.executeUpdate();
        		
        		// dodanie gracza do listy obecnych na serwerze graczy
        		plugin.playerslist.put(res.getString("player_name").toLowerCase(),
        							   new DooBStatPlayerData(res.getInt("id"), 
        									   	              res.getString("player_name"),
        									   	              curdate));
    			
    		} else {    			
    			int newid = this.plugin.db.addNewPlayer(
    					event.getPlayer().getName(),
    					curtimestamp,
    					event.getPlayer().getAddress().getAddress().getHostAddress());
        		
        		// dodanie danych gracza do listy obecnych na serwerze graczy
        		plugin.playerslist.put(event.getPlayer().getName().toLowerCase(),
        							   new DooBStatPlayerData(newid, 
        									   				  event.getPlayer().getName(),
        									   	              curdate));
    		}
    		

    	} catch(SQLException e) {
            e.printStackTrace();
        }
		
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		DooBStatPlayerData playerData = plugin.playerslist.get(event.getPlayer().getName().toLowerCase());
		
		// wykonaj tylko jeśli gracz istnieje w tabeli
		if(playerData != null) {
			
			plugin.playerslist.remove(event.getPlayer().getName().toLowerCase());
	
			this.plugin.db.savePlayerData(playerData);
			
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
		DooBStatPlayerData playerData = plugin.playerslist.get(player.getName().toLowerCase());
		Location playerLocation = player.getLocation();
		
        if(!playerLocation.getWorld().equals(event.getTo().getWorld())) return;
		
        double distance = event.getFrom().distance(event.getTo());
        
        if(distance == 0) return;
        
        // jeśli gracz (na) czymś jedzie
        if(player.isInsideVehicle()) {
        	
        	if(player.getVehicle() instanceof Vehicle) {
        		Vehicle vehicle = (Vehicle) player.getVehicle();
            	
            	if(vehicle.getType().equals(EntityType.PIG)) {
            		playerData.addDist(DooBStatPlayerData.PIG, distance);
            	} else if(vehicle.getType().equals(EntityType.MINECART)) {
            		playerData.addDist(DooBStatPlayerData.CART, distance);
            	} else if(vehicle.getType().equals(EntityType.BOAT)) {
            		playerData.addDist(DooBStatPlayerData.BOAT, distance);
            	} else if(vehicle.getType().equals(EntityType.HORSE)) {
            		playerData.addDist(DooBStatPlayerData.HORSE, distance);
            	}
        	}
        
        // jeśli gracz lata, pływa, lub idzie piechotą
        } else if(player.isFlying()) {
        	playerData.addDist(DooBStatPlayerData.FLY, distance);
        } else if(playerLocation.getBlock().getType().equals(Material.WATER) || 
        		playerLocation.getBlock().getType().equals(Material.STATIONARY_WATER)) {
        	playerData.addDist(DooBStatPlayerData.SWIM, distance);
        } else {
        	playerData.addDist(DooBStatPlayerData.FOOT, distance);
        }
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBedEnter(PlayerBedEnterEvent event) {
		DooBStatPlayerData playerData = plugin.playerslist.get(event.getPlayer().getName().toLowerCase());
		
		playerData.addBedEnter();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onFish(PlayerFishEvent event) {
		
		if(event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
			DooBStatPlayerData playerData = plugin.playerslist.get(event.getPlayer().getName().toLowerCase());
			playerData.addFish();
		}
		
		
	}
	
}
