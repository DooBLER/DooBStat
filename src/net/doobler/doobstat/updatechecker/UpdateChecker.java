package net.doobler.doobstat.updatechecker;

import net.doobler.doobstat.DooBStat;


public class UpdateChecker {
	
	private DooBStat plugin;
	private String filesFeed;
	
	public UpdateChecker(DooBStat plugin, String url) {
		this.plugin = plugin;
		this.filesFeed = url;

		new UpdateCheckerTask(this.plugin, this.filesFeed).runTaskAsynchronously(this.plugin);
	}
	
}
