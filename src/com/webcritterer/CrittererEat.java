
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
import java.io.FileOutputStream;


public class CrittererEat {

    public String[] args;
    public boolean bandwidthLimiter;
    public long avgKilobytesPerSecond;
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;
    //creates a timestamp for later use
    //long second = 1000; //1 second
    long neededSleepTime;
    long totalBytesRead;
    long totalDiffInTimestamps;
    long totalSleepTime;
    float truncatedValue;
    float theoreticalSleepTime;
    long maxBytesReadAtOnce;
    float totalTruncatedValue;
    float totalTheoryTime;
    long bytesRead;
    float avgBytesPerSec;
    long diffInTimestamps;
    public String errorMsg = "Error: Please Refer to ReadMe/Instructions";
    public String usageMsg = "Usage: ./run.sh <Seed URL or Text File Containing URLs> {(Optional in any order ) [--numbersofpagestoload=NUMBER of pages to download] [--bandwidthlimit=NUMBER average of KB/sec to critter at] [--stayindomain] [--dumpinsinglefile]}";
    long totalDigestTime;
    public boolean fileOutputClump;
    public String articlesClump;

    //public String pathToDir = "~/Users/hebeda_supreme/Desktop/";


    CrittererEat(String[] arguments, long bandwidthLimitValue, boolean fileOutputClump, boolean bandwidthLimiter){
        this.args = arguments;
        if (bandwidthLimiter) {
            avgKilobytesPerSecond = bandwidthLimitValue;
        }
        this.fileOutputClump = fileOutputClump;
        this.bandwidthLimiter = bandwidthLimiter;
    }


    public List<String> getLinks() {
        return this.links;
    }


    public Document critter(String url) {

        try {
            if (bandwidthLimiter) {
                URLConnection connection = new URL(url).openConnection();
                connection.addRequestProperty("User-Agent", "Chrome/67.0.3396.99");
                if (!connection.getContentType().equalsIgnoreCase("application/pdf")) {
                    System.out.println("Page from: " + url);
                    long previousTimestamp = System.currentTimeMillis(); //
                    InputStream iStream = connection.getInputStream();
                    long currentTimestamp = System.currentTimeMillis(); //
                    CountingInputStream someCountingStream = new CountingInputStream(iStream);
                    diffInTimestamps = currentTimestamp - previousTimestamp; //
                    System.out.println("Time Spent Downloading: " + diffInTimestamps); //
                    Document htmlDocument = Jsoup.parse(someCountingStream, null, url);
                    bytesRead = someCountingStream.getCount();
                    System.out.println("Bytes Read: " + bytesRead);
                    totalBytesRead += bytesRead;
                    totalDiffInTimestamps += diffInTimestamps; //
                    avgBytesPerSec = avgKilobytesPerSecond * 1024; //
                    recordNSleep(); //
                    this.htmlDocument = htmlDocument;

                    if(fileOutputClump) {
                        digestClump(htmlDocument);
                    } else {
                        digest(htmlDocument);
                    }

                } else {
                    String pdfURL = String.valueOf(url);
                    String[] splitPDFURL = pdfURL.split("//");
                    String pdfDomain = splitPDFURL[1];
                    String pdfName = pdfDomain.replaceAll("/", "_");

                    byte[] ba = new byte[1024];
                    int baLength;

                    FileOutputStream pdfFileStream = new FileOutputStream(pdfName + ".pdf");

                    // Read the PDF from the URL and save to a local file
                    long previousTimestamp = System.currentTimeMillis();
                    InputStream pdfStream = connection.getInputStream();
                    long currentTimestamp = System.currentTimeMillis();
                    CountingInputStream someCountingStream = new CountingInputStream(pdfStream);
                    diffInTimestamps = currentTimestamp - previousTimestamp; //
                    System.out.println("Time Spent Downloading: " + diffInTimestamps);
                    bytesRead = someCountingStream.getCount();
                    System.out.println("Bytes Read: " + bytesRead);
                    totalBytesRead += bytesRead;
                    totalDiffInTimestamps += diffInTimestamps; //
                    avgBytesPerSec = avgKilobytesPerSecond * 1024; //
                    recordNSleep();

                    while ((baLength = pdfStream.read(ba)) != -1) {
                        pdfFileStream.write(ba, 0, baLength);
                    }
                    pdfFileStream.flush();
                    pdfFileStream.close();
                    pdfStream.close();
                }

            } else {
                URLConnection connection = new URL(url).openConnection();
                connection.addRequestProperty("User-Agent", "Chrome/67.0.3396.99");
                if (!connection.getContentType().equalsIgnoreCase("application/pdf")) {
                    System.out.println("Page from: " + url);
                    InputStream iStream = connection.getInputStream();
                    CountingInputStream someCountingStream = new CountingInputStream(iStream);
                    Document htmlDocument = Jsoup.parse(someCountingStream, null, url);
                    long bytesRead = someCountingStream.getCount();
                    System.out.println("Bytes Read: " + bytesRead);
                    totalBytesRead += bytesRead;

                    if(fileOutputClump) {
                        digestClump(htmlDocument);
                    } else {
                        digest(htmlDocument);
                    }
                } else {
                    String pdfURL = String.valueOf(url);
                    String[] splitPDFURL = pdfURL.split("//");
                    String pdfDomain = splitPDFURL[1];
                    String pdfName = pdfDomain.replaceAll("/", "_");

                    byte[] ba = new byte[1024];
                    int baLength;

                    FileOutputStream pdfFileStream = new FileOutputStream(pdfName + ".pdf");

                    // Read the PDF from the URL and save to a local file
                    InputStream pdfStream = connection.getInputStream();
                    while ((baLength = pdfStream.read(ba)) != -1) {
                        pdfFileStream.write(ba, 0, baLength);
                    }
                    pdfFileStream.flush();
                    pdfFileStream.close();
                    pdfStream.close();
                }
            }

        } catch(IOException ioe) {
        } catch(java.lang.IllegalArgumentException emptyLineOnURLFile) {
            System.out.println(errorMsg);
            System.out.println(usageMsg);
        } catch(NullPointerException nu) {
        }
        return htmlDocument;
    }

    public void recordNSleep() {
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
            try {
                Thread.sleep(neededSleepTime);
            } catch (InterruptedException e) {
            }
        }

        if (truncatedValue > 0) {
            System.out.println("Truncated Time: " + truncatedValue);
            totalTruncatedValue += truncatedValue;
        }
        if (bytesRead > maxBytesReadAtOnce) {
            maxBytesReadAtOnce = bytesRead;
        }
    }

    public boolean digest(Document htmlDocument) {
        try {
            long digestTimestamp = System.currentTimeMillis();
            String filename = "<title></title>";
            Jsoup.parse(filename, "UTF-8");
            Elements titles = htmlDocument.select("title");
            PrintWriter pw = null;

            pw = new PrintWriter((titles.text()) + ".txt");

            for (Element title : titles)
                pw.println(title.text()); //print the title

            String html = "<html><head></head>" + "<body><p>" + "</p></body></html>";
            Jsoup.parse(html, "UTF-8");
            Elements paragraphs = htmlDocument.select("body");
            for (Element p : paragraphs)
                pw.println(p.text());
            //Collects links and adds them to list while counting number found
            pw.close();
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("**Grabbed (" + linksOnPage.size() + ") links***");
            for (Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));
                long digestEndTimestamp = System.currentTimeMillis();
                long digestTime = digestEndTimestamp - digestTimestamp;
                totalDigestTime += digestTime;
            }
            return true;

        } catch (FileNotFoundException e) {
        }

        return false;
    }

    public void digestClump(Document htmlDocument) {

        long digestTimestamp = System.currentTimeMillis();
        String filename = "<title></title>";
        Jsoup.parse(filename, "UTF-8");
        Elements titles = htmlDocument.select("title");
        String titleString = titles.text() + "\n\n";

        String html = "<html><head></head>" + "<body><p>" + "</p></body></html>";
        Jsoup.parse(html, "UTF-8");
        Elements paragraphs = htmlDocument.select("body");
        String paragraphString = paragraphs.text() + "\n\n";

        String singleArticle = titleString + paragraphString + "\n\n";
        articlesClump += singleArticle;

        Elements linksOnPage = htmlDocument.select("a[href]");
        System.out.println("**Grabbed (" + linksOnPage.size() + ") links***");
        for (Element link : linksOnPage) {
            this.links.add(link.absUrl("href"));
            long digestEndTimestamp = System.currentTimeMillis();
            long digestTime = digestEndTimestamp - digestTimestamp;
            totalDigestTime += digestTime;
        }
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

    public long getTotalDigestTime() {
        return totalDigestTime;
    }

    public String getArticlesClump() {
        return articlesClump;
    }
}