package com.webcritterer;

import org.apache.commons.io.input.CountingInputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class CrittererEat

{
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;
    long avgKilobytesPerSecond = 720; //ASSIGN THE MAXIMUM NUMBER OF KILOBYTES PER SECOND HERE (BANDWIDTH CONSUMPTION)
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

    public boolean critter(String url) {
        try {
            URLConnection connection = new URL(url).openConnection(); //create a URL connection to the URL
            long previousTimestamp = System.currentTimeMillis();
            InputStream iStream = connection.getInputStream();
            long currentTimestamp = System.currentTimeMillis();
            CountingInputStream someCountingStream = new CountingInputStream(iStream);
            long diffInTimestamps = currentTimestamp - previousTimestamp;
            System.out.println("Time Spent Downloading: " + diffInTimestamps);
            //CrittererBandwidthLimitation in = new CrittererBandwidthLimitation(iStream); //Creates a new Bandwidth limiter which will inherit the inputstream
            //String htmlText = org.apache.commons.io.IOUtils.toString(iStream, connection.getContentEncoding()); //The inputstream having returned from the limiter, will be taken as a string
            Document htmlDocument = Jsoup.parse(someCountingStream, null, url);
            long bytesRead = someCountingStream.getCount();
            System.out.println("Bytes Read: " + bytesRead);
            totalBytesRead += bytesRead;
            totalDiffInTimestamps += diffInTimestamps;

            theoreticalSleepTime = (((bytesRead * 1000)/avgBytesPerSec) - diffInTimestamps);
            neededSleepTime = (long) (theoreticalSleepTime);
            truncatedValue = (theoreticalSleepTime - neededSleepTime);

            if(theoreticalSleepTime > 0){
                System.out.println("Theoretical Sleep Time: " + theoreticalSleepTime);
                totalTheoryTime += theoreticalSleepTime;
            }

            if(neededSleepTime > 0) {
                totalSleepTime += neededSleepTime;
                System.out.println("neededSleepTime" + neededSleepTime);
                Thread.sleep(neededSleepTime);
            }
            if(truncatedValue > 0) {
                System.out.println("Truncated Time: " + truncatedValue);
                totalTruncatedValue += truncatedValue;
            }
            if(bytesRead > maxBytesReadAtOnce) {
                maxBytesReadAtOnce = bytesRead;
            }

            this.htmlDocument = htmlDocument;
            //Will parse content from <title> headers and make them titles of printed text documents
            String filename = "<title></title>";
            Jsoup.parse(filename);
            Elements titles = htmlDocument.select("title");
            PrintWriter pw = new PrintWriter((/*pathToDir +*/ titles.text()) + ".txt");
            //Set the printstream out to a text file
            pw.println("From page at:" + url); //print a message

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
        } catch (IOException ioe) {
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    return false;
    }

    public long gettotalBytesRead(){
        return totalBytesRead;
    }

    public long gettotalDiffInTimestamps(){
        return totalDiffInTimestamps;
    }

    public long gettotalSleepTime(){
        return totalSleepTime;
    }

    public float gettotalTruncatedValue(){
        return totalTruncatedValue;
    }

    public float gettotalTheoryTime(){
        return totalTheoryTime;
    }

    public long getmaxBytesReadAtOnce(){
        return maxBytesReadAtOnce;
    }
}

