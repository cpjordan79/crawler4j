Chris's Crawler4j Mod
=============
First off, massive credit should be given to [Yasser Ganjisaffar](http://www.ics.uci.edu/~yganjisa/) 
for developing the original crawler4j code and making it available on [google code](http://code.google.com/p/crawler4j/).

What I am doing in this fork is extending crawler4j to be a little more flexible for application development. Key things
that I am working on are as follows:

*   Allow WebCrawlers to have dynamically assigned configurations (Done)
*   Improve thread management in the crawler controller (to come)

Configurable WebCrawler
-------------
So a key enhancement with the WebCrawler class is the addition of the configs Map. It is a pretty simple attribute to have
added to it provides subclasses with a mechanism for being configured dynamically by a Controller. The ConfigurableCrawlController
does exactly that.

By permitting dynamic configurations, that means a WebCrawler can be configured by an application as opposed to a properties file.
Using a properties file is fine if you are developing a stand alone job however, it is problematic if you are trying to develop
an application that instantiates crawlers and needs to configure them on the fly which is that I am with a few my other projects.
