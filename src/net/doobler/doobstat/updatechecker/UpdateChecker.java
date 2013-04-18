package net.doobler.doobstat.updatechecker;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import net.doobler.doobstat.DooBStat;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class UpdateChecker {
	
	private DooBStat plugin;
	private URL filesFeed;
	private String version;
	private String link;
	
	public UpdateChecker(DooBStat plugin, String url) {
		this.plugin = plugin;
		
		try {
			this.filesFeed = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}
	
	public boolean updateNeeded() {
		long timer = 0;
		
		try {
			InputStream input = this.filesFeed.openConnection().getInputStream();
			
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
			
			Node latestFile = document.getElementsByTagName("item").item(0);

			NodeList children = latestFile.getChildNodes();
			
			this.version = children.item(1).getTextContent().replaceAll("[ \na-zA-Z_-]", "");
			this.link = children.item(3).getTextContent();
			
			if(!this.plugin.getDescription().getVersion().equals(this.version)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public String getLink() {
		return this.link;
	}
}
