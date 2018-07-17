# WebCritterer

## Specifications
WebCritterer is a simple web crawler that opens a connection to and travels to a given seed URL, and proceeds to download and parse the html to extract text from the page. Text from pages is downloaded as text files with the titles corresponding to the webpage. The Critterer has a collection of URLs to travel to, and will follow that list of unique URLs until termination (maximum number of pages hit; out of URLs). The Critterer is also capable of bandwidth/network load restrictions. 

## Caution Regarding Bandwidth Limiter
While the Critterer can have its bandwidth restricted, this feature is extremely limited. The bandwidth restriction class uses sleep intervals in a stream wrapper around the connection to the webpage. Because this cannot access sockets, the file is downloaded in memory first before data is extracted and parsed, the bandwidth usage spikes, and the stream wrapper can only serve to slow the flow of bytes per second to stay under a limit. This results in the actual bandwidth being dramatically under the limit (around 50% to 95% lower). This is also faced with the spikes in bandwidth usage, which mostly stay under and close to the assigned limit. However, when given a lower KB/sec limit (for example 10 kb/sec), there are dramatic spikes that go 200% above the assigned limit. The limit is more effective when assigned to a higher value.

## Deployment
* The Critterer's URL specification is in the CrittererStarter class, or the main class of the program, and can be changed inside the "critterer.load()" quotes.
* Maximum number of pages to travel to is in the Critterer class and can be changed at the "public static final long MaximumPagesToGoTo =" line.
* Bandwidth limit is in the CrittererBandwidthLimitation class and can be changed at the "long maxKilobytesPerSecond =" line (in Kilobytes).


## Authors

* **Michael Li** - *Initial work* -

See also the list of [contributors]

