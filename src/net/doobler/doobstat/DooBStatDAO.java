package net.doobler.doobstat;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa Data Access Object
 * 
 * Rozszerza klasę dostępu do bazy MySQL.
 * Zawiera SQL dla preparedStatement itp.
 * 
 * @author DooBLER
 *
 */
public class DooBStatDAO extends MySQL {
	
	// Zmienne do przechowywania zapytań i gotowych PreparedStatements
	private Map<String, String> prepSQL = new HashMap<String, String>();
	private Map<String, PreparedStatement> prepStat = new HashMap<String, PreparedStatement>();
	
	public DooBStatDAO(DooBStat plugin, String hostname, String portnmbr,
			String database, String username, String password, String tblprefix) {
		super(plugin, hostname, portnmbr, database, username, password, tblprefix);
		
		
		// sprawdzenie czy trzeba utworzyć tabele w bazie
		if(!this.tableExists(this.getPrefixed("players"))) {
			this.createTables();
		}
		
		
		// dodaje SQL do listy dla Prepared statement
		this.addPrepSQL();
	}

	
	
	public PreparedStatement getPreparedStatement(String name) {
		
		Connection conn = this.getConn();
		
		boolean exists = this.prepStat.containsKey(name);
		boolean isClosed = true;
		boolean isConn = false;
		
		PreparedStatement prest = null;

		// jeśli istnieje
		if(exists) {
			
			prest = this.prepStat.get(name);
			
			try {
                isClosed = prest.isClosed();
            }
            catch (SQLException e) {
                isClosed = true;
            }
			
			try {
				isConn = (prest.getConnection() == conn);
            }
            catch (SQLException e) {
            	isConn = false;
            }
		}
		
		
		if (!exists || !isConn || isClosed) {
			try {
				prest = conn.prepareStatement(this.prepSQL.get(name));
				this.prepStat.put(name, prest);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			prest.clearParameters();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prest; 
	}
	
	
	/**
	 * Dodaje SQL do listy, z której powstaną PreparedStatement 
	 * 
	 * @param name Nazwa pod jaką będzie dostępny PreparedStatement
	 * @param sql SQL który zostanie użyty do stworzenia PreparedStatement
	 */
	public void addStatementSQL(String name, String sql) {
		this.prepSQL.put(name, sql);
	}
	
	
	/**
	 * Funkcja istnieje aby zebrać w jednym miejscu SQL dla PreparedStatement
	 */
	private void addPrepSQL() {
		
		// pobiera dane gracza na podstawie nicku
		this.addStatementSQL("getPlayerByName",
				"SELECT * " +
				"FROM " + this.getPrefixed("players") + " " +
				"WHERE LOWER(`player_name`) = LOWER(?)" +
				"LIMIT 1");
		
		// aktualizuje dane gracza przy wchodzeniu na serwer
        this.addStatementSQL("updatePlayerJoin",
        		"UPDATE " + this.getPrefixed("players") + " " +
				"SET " +
				"online = 1, " +
				"last_login = ?, " +
				"num_logins = num_logins + 1, " +
				"this_login = ?" +
				"WHERE id = ?");
		
		// aktualizuje dane gracza przy wychodzeniu z serwera
        this.addStatementSQL("updatePlayerQuit",
        		"UPDATE " + this.getPrefixed("players") + " " +
				"SET " +
				"online = 0, " +
				"last_logout = ?," +
				"num_secs_loggedon = num_secs_loggedon + ? " +
				"WHERE id = ?");
	}
	
	
	/**
	 * Zwraca listę nazw graczy do usunięcia z powodu przekroczenia czasu podanego w configu
	 * 
	 * @return List<String> z nazwami graczy do usunięcia
	 */
	public List<String> getCleanNames() {
		List<String> player_names = new ArrayList<String>();
		
		Date curdate = new Date();
		Timestamp olderthan = new Timestamp(curdate.getTime() - 
				(this.plugin.getConfig().getInt("clean.days")*24*3600*(long)1000));
		
		Connection conn = this.getConn();
		
		String sql = "SELECT player_name FROM " + this.getPrefixed("players") + " " +
				"WHERE this_login < ?";
		
		try {
			PreparedStatement prest = conn.prepareStatement(sql);
			prest.setTimestamp(1, olderthan);
			ResultSet players_set = prest.executeQuery();
			
			while(players_set.next()) {
				player_names.add(players_set.getString("player_name"));
			}
			
			prest.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return player_names;
	}
	
	
	/**
	 * Funkcja usuwa danego gracza z bazy DooBStat
	 * 
	 * @param player_name
	 */
	public boolean removePlayer(String player_name) {
		Connection conn = this.getConn();
		
		String sql = "DELETE FROM " + this.getPrefixed("players") + " " +
				"WHERE player_name = ?" +
				"LIMIT 1";
		
		int delrows = 0;
		try {
			PreparedStatement prest = conn.prepareStatement(sql);
			prest.setString(1, player_name);
			delrows = prest.executeUpdate();
			prest.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(delrows < 1) {
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * Tworzy strukturę tabel pluginu
	 */
	public void createTables() {
		
		Connection conn = this.getConn();
		
		String sql = "CREATE TABLE IF NOT EXISTS `" + this.getPrefixed("players") + "` (" +
				 "`id` int(11) NOT NULL AUTO_INCREMENT, " +
				 "`player_name` varchar(20) NOT NULL, " +
				 "`online` tinyint(1) NOT NULL, " +
				 "`firstever_login` datetime NOT NULL, " +
				 "`last_login` datetime DEFAULT NULL, " +
				 "`num_logins` int(11) NOT NULL, " +
				 "`this_login` datetime DEFAULT NULL, " +
				 "`last_logout` datetime DEFAULT NULL, " +
				 "`num_secs_loggedon` int(11) NOT NULL, " +
				 "PRIMARY KEY (`id`), " +
				 "UNIQUE KEY `player_name` (`player_name`) " +
				 ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";
		
		try {
			Statement statement = conn.createStatement();
			statement.executeUpdate(sql);
			statement.close();
			this.plugin.getLogger().info("DB tables created.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
