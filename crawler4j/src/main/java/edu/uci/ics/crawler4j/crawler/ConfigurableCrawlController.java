package edu.uci.ics.crawler4j.crawler;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.uci.ics.crawler4j.frontier.Frontier;

public class ConfigurableCrawlController extends CrawlController {

    private Map<String,Object> crawlerConfigs;
    
    private static final Logger logger = Logger.getLogger(ConfigurableCrawlController.class);
    
    public ConfigurableCrawlController(String storageFolder, boolean resumable, Map<String,Object> crawlerConfigs) throws Exception {
        this(storageFolder, resumable);
        setCrawlerConfigs(crawlerConfigs);
    }
    
    public ConfigurableCrawlController(String storageFolder, boolean resumable) throws Exception {
        super(storageFolder, resumable);
    }

    public ConfigurableCrawlController(String storageFolder) throws Exception {
        super(storageFolder);
    }

    @Override
    public <T extends WebCrawler> void start(Class<T> _c, int numberOfCrawlers) {
        try {
            crawlersLocalData.clear();
            threads = new ArrayList<Thread>();
            List<T> crawlers = new ArrayList<T>();
            int numberofCrawlers = numberOfCrawlers;
            for (int i = 1; i <= numberofCrawlers; i++) {
                maxCrawlerId = i;
                T crawler = createCrawler(_c, maxCrawlerId);
                crawler.getThread().start();
                crawlers.add(crawler);
                threads.add(crawler.getThread());
                logger.info("Crawler " + maxCrawlerId + " started.");
            }
            while (true) {
                sleep(10);
                boolean someoneIsWorking = false;
                for (int i = 0; i < threads.size(); i++) {
                    Thread thread = threads.get(i);
                    if (!thread.isAlive()) {
                        logger.info("Thread " + i + " was dead, I'll recreate it.");
                        maxCrawlerId ++;
                        T crawler = createCrawler(_c, maxCrawlerId);
                        crawler.getThread().start();
                        
                        threads.remove(i);
                        threads.add(i, crawler.getThread());
                        
                        crawlers.remove(i);
                        crawlers.add(i, crawler);
                    } else if (thread.getState() == State.RUNNABLE) {
                        someoneIsWorking = true;
                    }
                }
                if (!someoneIsWorking) {
                    // Make sure again that none of the threads are alive.
                    logger.info("It looks like no thread is working, waiting for 40 seconds to make sure...");
                    sleep(40);

                    if (!isAnyThreadWorking()) {
                        long queueLength = Frontier.getQueueLength();
                        if (queueLength > 0) {
                            continue;
                        }
                        logger.info("No thread is working and no more URLs are in queue waiting for another 60 seconds to make sure...");
                        sleep(60);
                        queueLength = Frontier.getQueueLength();
                        if (queueLength > 0) {
                            continue;
                        }
                        logger.info("All of the crawlers are stopped. Finishing the process...");
                        for (T crawler : crawlers) {
                            crawler.onBeforeExit();
                            crawlersLocalData.add(crawler.getMyLocalData());
                        }

                        // At this step, frontier notifies the threads that were waiting for new URLs and they should stop
                        // We will wait a few seconds for them and then return.
                        Frontier.finish();
                        logger.info("Waiting for 10 seconds before final clean up...");
                        sleep(10);

                        Frontier.close();
                        PageFetcher.stopConnectionMonitorThread();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("error encountered while crawling", e);
            throw new RuntimeException("error encountered while crawling", e);
        }
    }
    
    protected <T extends WebCrawler> T createCrawler(Class<T> _c, int id) throws InstantiationException, IllegalAccessException {
        T crawler = _c.newInstance();
        Thread thread = new Thread(crawler, "Crawler " + id);
        crawler.setThread(thread);
        crawler.setMyId(id);
        crawler.setMyController(this);
        crawler.setConfigs(getCrawlerConfigs());
        
        return crawler;
    }

    public Map<String, Object> getCrawlerConfigs() {
        return crawlerConfigs;
    }

    public void setCrawlerConfigs(Map<String, Object> crawlerConfigs) {
        this.crawlerConfigs = crawlerConfigs;
    }
}
