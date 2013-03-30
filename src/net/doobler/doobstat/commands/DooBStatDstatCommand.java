package net.doobler.doobstat.commands;


import net.doobler.doobstat.DooBStat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DooBStatDstatCommand implements CommandExecutor {

	// Zmienna przechowuje referencję do głównego obiektu pluginu.
	protected DooBStat plugin;
	
	public DooBStatDstatCommand(DooBStat plugin) {
		this.plugin = plugin;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		if (args.length > 0) {
			if(!sender.hasPermission("dstat.dstat.clean")) {
				sender.sendMessage("You don't have dstat.dstat.clean");
				return false;
			}
            if(args[0].equalsIgnoreCase("clean")) {
            	
            	int rows = this.plugin.db.cleanDB();
            	if(rows > 0) {
            		this.plugin.getLogger().info(rows +
            				" old entries deleted. (by " + sender.getName() + ")");
            	}
            	
            	if(sender instanceof Player) {
            		 Player player = (Player) sender;
            		 player.sendMessage(rows + " old entries deleted.");
            	}
            	
            	return true;
            }
        } 

		return false;
	}
	
	

}
