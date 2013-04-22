package net.doobler.doobstat.commands;


import net.doobler.doobstat.DooBStat;
import net.doobler.doobstat.utils.CleanPlayersTask;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
            	if(!CleanPlayersTask.is_working) {
            		new CleanPlayersTask(this.plugin).runTask(this.plugin);
            	} else {
            		this.plugin.getLogger().info("Cleaning is in progress." +
            				"You cannot start this command now.");
            	}
            	return true;
            }
        } 

		return false;
	}
	
	

}
