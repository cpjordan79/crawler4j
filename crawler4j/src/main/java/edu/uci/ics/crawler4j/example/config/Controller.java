package edu.uci.ics.crawler4j.example.config;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.crawler4j.crawler.ConfigurableCrawlController;

public class Controller {

        public static void main(String[] args) throws Exception {
            if (args.length < 2) {
                System.out.println("Please specify 'root folder' and 'number of crawlers'.");
                return;
            }
            
            /*
             * rootfolder is a folder where intermediate crawl data is
             * stored. 
             */
            String rootFolder = args[0];
            
            /*
             * numberOfCrawlers shows the number of concurrent threads
             * that should be initiated for crawling.
             */
            int numberOfCrawlers = Integer.parseInt(args[1]);
            
            /*
             * The ConfigurableCrawlController has a Map property, crawlerConfigs, that can be
             * set. This map is passed to each WebCrawler it instantiates. It is up to subclasses
             * of WebCrawler to make use of the crawlerConfigs map. WebCrawler itself does not use
             * the crawlerConfigs.
             */
            ConfigurableCrawlController controller = new ConfigurableCrawlController(rootFolder);
            
            /*
             * Creating a crawlerConfigs map that will be passed to WebCrawlers instantiated by
             * the ConfigurableCrawlerController. 
             */
            HashMap<String,Object> crawlerConfigs = new HashMap<String,Object>();
            ArrayList<String> acceptableURLs = new ArrayList<String>();
            acceptableURLs.add("http://www.ics.uci.edu/~yganjisa/");
            acceptableURLs.add("http://www.ics.uci.edu/~lopes/");
            crawlerConfigs.put("acceptableURLs", acceptableURLs);
            controller.setCrawlerConfigs(crawlerConfigs);
            
            controller.addSeed("http://www.ics.uci.edu/~yganjisa/");
            controller.addSeed("http://www.ics.uci.edu/~lopes/");
            controller.addSeed("http://www.ics.uci.edu/");
            
            controller.setPolitenessDelay(200);
            controller.setMaximumCrawlDepth(2);
            controller.setMaximumPagesToFetch(100);
            
            controller.start(MyCrawler.class, numberOfCrawlers);
        }

}

