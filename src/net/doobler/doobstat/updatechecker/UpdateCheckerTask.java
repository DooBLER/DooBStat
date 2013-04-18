package net.doobler.doobstat.updatechecker;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateCheckerTask extends BukkitRunnable {

	private final JavaPlugin plugin;
	private URL filesFeed;
	private String version;
	private String link;
	
	public UpdateCheckerTask(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		
		try {
			InputStream input = this.filesFeed.openConnection().getInputStream();
			
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
			
			Node latestFile = document.getElementsByTagName("item").item(0);

			NodeList children = latestFile.getChildNodes();
			
			this.version = children.item(1).getTextContent().replaceAll("[ \na-zA-Z_-]", "");
			this.link = children.item(3).getTextContent();
			
			if(!this.plugin.getDescription().getVersion().equals(this.version)) {
				plugin.getLogger().info("----====#### Jest nowa wersja. ####====----");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
