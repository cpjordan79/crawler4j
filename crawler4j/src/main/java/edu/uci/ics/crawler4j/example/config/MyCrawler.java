/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.uci.ics.crawler4j.example.config;

import java.util.List;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {

    Pattern filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
            + "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    public MyCrawler() {
    }

    public boolean shouldVisit(WebURL url) {
        String href = url.getURL().toLowerCase();
        if (filters.matcher(href).matches()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<String> acceptableUrls = (List<String>) getConfigs().get("acceptableURLs");
        if (acceptableUrls != null) {
            for (String acceptableUrl : acceptableUrls) {
                if (href.startsWith(acceptableUrl)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void visit(Page page) {
        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();         
        String text = page.getText();
        List<WebURL> links = page.getURLs();
        int parentDocid = page.getWebURL().getParentDocid();
        
        /*
         * Using the crawlerConfigs to check for acceptable urls. Only
         * these urls will be processed.
         */
        @SuppressWarnings("unchecked")
        List<String> acceptableURLs = (List<String>)getConfigs().get("acceptableURLs");
        if (acceptableURLs != null) {
            for (String urlPrefix : acceptableURLs) {
                if (url.startsWith(urlPrefix)) {
                    System.out.println("Found acceptable URL!");
                    System.out.println("Docid: " + docid);
                    System.out.println("URL: " + url);
                    System.out.println("Text length: " + text.length());
                    System.out.println("Number of links: " + links.size());
                    System.out.println("Docid of parent page: " + parentDocid);
                    System.out.println("=============");
                    
                    break;
                }
            }
        }
    }    
}
