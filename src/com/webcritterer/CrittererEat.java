package com.webcritterer;

import org.apache.commons.io.input.CountingInputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class CrittererEat

{
    CrittererEat(String[] arguments){
        this.args = arguments;
        if (args[3].matches("[0-9]+")) {
            this.bandwidthLimiterOptions = "true";
            this.bandwidthLimiter = Boolean.parseBoolean(bandwidthLimiterOptions);
            this.assignedAvgKBS = args[3];
            this.avgKilobytesPerSecond = Long.parseLong(assignedAvgKBS);
        } else {
            this.bandwidthLimiterOptions = "false";
            this.bandwidthLimiter = Boolean.parseBoolean(bandwidthLimiterOptions);
        }
    }

    public String[] args;
    public String bandwidthLimiterOptions;
    public boolean bandwidthLimiter;
    public String assignedAvgKBS;
    public long avgKilobytesPerSecond;
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;
    //creates a timestamp for later use
    long second = 1000; //1 second
    float avgBytesPerSec = avgKilobytesPerSecond * 1024;
    long neededSleepTime;
    long totalBytesRead;
    long totalDiffInTimestamps;
    long totalSleepTime;
    float truncatedValue;
    float theoreticalSleepTime;
    long maxBytesReadAtOnce;
    float totalTruncatedValue;
    float totalTheoryTime;
    //public String pathToDir = "~/Users/hebeda_supreme/Desktop/";


    public List<String> getLinks() {
        return this.links;
    }

    public Document critter(String url) {
        try {
            if (bandwidthLimiter) {
                URLConnection connection = new URL(url).openConnection(); //create a URL connection to the URL
                long previousTimestamp = System.currentTimeMillis();
                InputStream iStream = connection.getInputStream();
                long currentTimestamp = System.currentTimeMillis();
                CountingInputStream someCountingStream = new CountingInputStream(iStream);
                long diffInTimestamps = currentTimestamp - previousTimestamp;
                System.out.println("Time Spent Downloading: " + diffInTimestamps);
                Document htmlDocument = Jsoup.parse(someCountingStream, null, url);
                long bytesRead = someCountingStream.getCount();
                System.out.println("Bytes Read: " + bytesRead);
                totalBytesRead += bytesRead;
                totalDiffInTimestamps += diffInTimestamps;

                theoreticalSleepTime = (((bytesRead * 1000) / avgBytesPerSec) - diffInTimestamps);
                neededSleepTime = (long) (theoreticalSleepTime);
                truncatedValue = (theoreticalSleepTime - neededSleepTime);

                if (theoreticalSleepTime > 0) {
                    System.out.println("Theoretical Sleep Time: " + theoreticalSleepTime);
                    totalTheoryTime += theoreticalSleepTime;
                }

                if (neededSleepTime > 0) {
                    totalSleepTime += neededSleepTime;
                    System.out.println("neededSleepTime" + neededSleepTime);
                    Thread.sleep(neededSleepTime);
                }

                if (truncatedValue > 0) {
                    System.out.println("Truncated Time: " + truncatedValue);
                    totalTruncatedValue += truncatedValue;
                }
                if (bytesRead > maxBytesReadAtOnce) {
                    maxBytesReadAtOnce = bytesRead;
                }

                this.htmlDocument = htmlDocument;

            } else {
                URLConnection connection = new URL(url).openConnection(); //create a URL connection to the URL
                InputStream iStream = connection.getInputStream();
                CountingInputStream someCountingStream = new CountingInputStream(iStream);
                Document htmlDocument = Jsoup.parse(someCountingStream, null, url);
                long bytesRead = someCountingStream.getCount();
                System.out.println("Bytes Read: " + bytesRead);
                totalBytesRead += bytesRead;
                this.htmlDocument = htmlDocument;

                if (bytesRead > maxBytesReadAtOnce) {
                    maxBytesReadAtOnce = bytesRead;
                }
            }
            digest(htmlDocument);

        } catch (IOException ioe) {

        } catch (InterruptedException e) {

        }
        return htmlDocument;
    }

    public boolean digest(Document htmlDocument) {
        //Will parse content from <title> headers and make them titles of printed text documents
        try {
            String filename = "<title></title>";
            Jsoup.parse(filename);
            Elements titles = htmlDocument.select("title");
            PrintWriter pw = null;

            pw = new PrintWriter((titles.text()) + ".txt");

            //Set the printstream out to a text file
            //pw.println("From page at:" + url); print a message

            for (Element title : titles)
                pw.println(title.text()); //print the title

            //Parses the content from the page and prints it into text file
            String html = "<html><head></head>" + "<body><p>" + "</p></body></html>";
            Jsoup.parse(html);
            Elements paragraphs = htmlDocument.select("p");
            for (Element p : paragraphs)
                pw.println(p.text());
            //Collects links and adds them to list while counting number found
            pw.close();
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("**Grabbed (" + linksOnPage.size() + ") links***");
            for (Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));
            }
            return true;

        } catch (FileNotFoundException e) {

        }
        return false;
    }

    public long gettotalBytesRead() {
        return totalBytesRead;
    }

    public long gettotalDiffInTimestamps() {
        return totalDiffInTimestamps;
    }

    public long gettotalSleepTime() {
        return totalSleepTime;
    }

    public float gettotalTruncatedValue() {
        return totalTruncatedValue;
    }

    public float gettotalTheoryTime() {
        return totalTheoryTime;
    }

    public long getmaxBytesReadAtOnce() {
        return maxBytesReadAtOnce;
    }
}

