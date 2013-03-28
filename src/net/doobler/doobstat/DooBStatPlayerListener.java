package net.doobler.doobstat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class DooBStatPlayerListener implements Listener {
	
	public DooBStat plugin;
	
	public DooBStatPlayerListener(DooBStat plugin) {
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

    			prest2.setString(1, res.getString("this_login"));
    			prest2.setTimestamp(2, curtimestamp);
    			prest2.setInt(3, res.getInt("id"));
    			prest2.executeUpdate();
        		
        		// dodanie gracza do listy obecnych na serwerze graczy
        		plugin.playerslist.put(res.getString("player_name").toLowerCase(),
        							   new DooBStatPlayerData(res.getInt("id"), 
        									   	              res.getString("player_name"),
        									   	              curdate));
    			
    		} else {
    			Connection conn = this.plugin.db.getConn();
    			
    			// dodanie nowego gracza do bazy
    			// PreparedStatemnt nie jest zapisany, bo dodawanie nowych graczy
    			// występuje relatywnie dużo rzadziej 
    			String sql = "INSERT INTO " + plugin.db.getPrefixed("players") +
    				  " SET " +
    				  "player_name = ?, " +
    				  "online = 1, " +
					  "firstever_login = ?, " +
					  "last_login = ?, " +
					  "num_logins = 1, " +
					  "this_login = ?, " +
					  "num_secs_loggedon = 1";
    			
    			PreparedStatement prest2 = conn.prepareStatement(sql,
    					Statement.RETURN_GENERATED_KEYS);
        		prest2.setString(1, event.getPlayer().getName());
        		prest2.setTimestamp(2, curtimestamp);
        		prest2.setTimestamp(3, curtimestamp);
        		prest2.setTimestamp(4, curtimestamp);
        		prest2.executeUpdate();
        		
        		ResultSet rs = prest2.getGeneratedKeys();
        		int newid = 0;
        		if (rs.next()){
        		    newid = rs.getInt(1);
        		}
        		
        		prest2.close();
        		
        		// dodanie danych gracza do listy obecnych na serwerze graczy
        		plugin.playerslist.put(event.getPlayer().getName().toLowerCase(),
        							   new DooBStatPlayerData(newid, 
        									   				  event.getPlayer().getName(),
        									   	              curdate));
    		}
    		

    	} catch(SQLException e) {
            e.printStackTrace();
        }
		
		

		event.getPlayer().sendMessage("Czesc " + event.getPlayer().getName() +
				", DooBStat test: " + plugin.getConfig().getString("version"));
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		Date curdate = new Date();
		Timestamp curtimestamp = new Timestamp(curdate.getTime());
		
		DooBStatPlayerData player = plugin.playerslist.get(event.getPlayer().getName().toLowerCase());
		
		// wykonaj tylko jeśli gracz istnieje w tabeli
		if(player != null) {
			
			plugin.playerslist.remove(event.getPlayer().getName().toLowerCase());
	
			PreparedStatement prest = this.plugin.db.getPreparedStatement("updatePlayerQuit");
			
			try {
				prest.setTimestamp(1, curtimestamp);
				prest.setInt(2, (int)((curdate.getTime() - player.getLoginDate().getTime())/1000));
				prest.setInt(3, player.getPlayerId());
				prest.executeUpdate();
				
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
