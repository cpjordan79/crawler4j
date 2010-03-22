package edu.uci.ics.crawler4j.crawler;

import it.unimi.dsi.parser.BulletParser;
import it.unimi.dsi.parser.callback.LinkExtractor;
import it.unimi.dsi.parser.callback.TextExtractor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.crawler4j.url.URLCanonicalizer;

public class HTMLParser {

	private String text;
	
	private String title;

	private BulletParser bulletParser = new BulletParser();

	private TextExtractor textExtractor = new TextExtractor();

	private LinkExtractor linkExtractor = new LinkExtractor();

	Set<String> urls;

	public void parse(String htmlContent, String contextURL) {		
		urls = new HashSet<String>();
		char[] chars = htmlContent.toCharArray();

		bulletParser.setCallback(textExtractor);
		bulletParser.parse(chars);
		text = textExtractor.text.toString().trim();
		title = textExtractor.title.toString().trim();
		
		bulletParser.setCallback(linkExtractor);
		bulletParser.parse(chars);
		Iterator<String> it = linkExtractor.urls.iterator();

		int urlCount = 0;
		while (it.hasNext()) {
			String href = it.next();
			href = href.trim();
			if (href.length() == 0) {
				continue;
			}			
			String hrefWithoutProtocol = href;
			if (href.startsWith("http://")) {
				hrefWithoutProtocol = href.substring(7);
			} 
			if (hrefWithoutProtocol.indexOf(":") < 0
					&& hrefWithoutProtocol.indexOf("@") < 0) {
				urls.add(URLCanonicalizer.getCanonicalURL(href, contextURL).toExternalForm());
				urlCount++;
				if (urlCount > Config.maxOutLinks) {
					break;
				}
			}			
		}
	}

	public String getText() {
		return text;
	}
	
	public String getTitle() {
		return title;
	}	

	public Set<String> getLinks() {
		return urls;
	}	
}
