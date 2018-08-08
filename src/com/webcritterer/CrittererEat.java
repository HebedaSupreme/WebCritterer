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
            URLConnection connection = new URL(url).openConnection();
            connection.addRequestProperty("User-Agent", "Chrome/67.0.3396.99");
            System.out.println("Page from: " + url);
            if (!connection.getContentType().equalsIgnoreCase("application/pdf")) {
                loadNMeasure(connection, url);
                if(bandwidthLimiter) {
                    recordNSleep();
                }
                ifClump();

            } else {
                pdfstreamer(connection.getInputStream(), pdfstarter(url));
                if(bandwidthLimiter) {
                    recordNSleep();
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

    public InputStream loadNMeasure(URLConnection connection, String url) {
        long previousTimestamp = System.currentTimeMillis();
        InputStream iStream = null;
        try {
            iStream = connection.getInputStream();
        } catch (IOException e) {
        }
        long currentTimestamp = System.currentTimeMillis(); //
        CountingInputStream someCountingStream = new CountingInputStream(iStream);
        try {
            htmlDocument = Jsoup.parse(someCountingStream, null, url);
        } catch (IOException e) {
        }
        diffInTimestamps = currentTimestamp - previousTimestamp; //
        System.out.println("Time Spent Downloading: " + diffInTimestamps); //
        bytesRead = someCountingStream.getCount(); //
        System.out.println("Bytes Read: " + bytesRead); //
        totalBytesRead += bytesRead; //
        totalDiffInTimestamps += diffInTimestamps; //
        avgBytesPerSec = avgKilobytesPerSecond * 1024; //
        return iStream;
    }

    public FileOutputStream pdfstarter(String url) {
        String pdfURL = String.valueOf(url); //
        String[] splitPDFURL = pdfURL.split("//"); //
        String pdfDomain = splitPDFURL[1]; //
        String pdfName = pdfDomain.replaceAll("/", "_"); //
        FileOutputStream pdfFileStream = null; //
        try {
            pdfFileStream = new FileOutputStream(pdfName + ".pdf");
        } catch (FileNotFoundException e) {
        }
        return pdfFileStream;
    }

    public void pdfstreamer(InputStream pdfStream, FileOutputStream pdfFileStream) {
        try {
            byte[] ba = new byte[1024];
            int baLength;

            while ((baLength = pdfStream.read(ba)) != -1) {
                pdfFileStream.write(ba, 0, baLength);
            }
            pdfFileStream.flush();
            pdfFileStream.close();
            pdfStream.close();
        } catch(IOException ioe) {
        }
    }

    public void ifClump() {
        if(fileOutputClump) {
            digestClump(htmlDocument);
        } else {
            digest(htmlDocument);
        }
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
