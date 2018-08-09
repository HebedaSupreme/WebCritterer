package com.webcritterer;

import org.apache.commons.io.input.CountingInputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.io.FileOutputStream;


public class CrittererEat {

    public String[] args;
    public boolean bandwidthLimiter;
    public long avgKilobytesPerSecond;
    private List<String> links = new LinkedList<String>();
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

    public void critter(String url) {
        links.clear();
        try {
            HttpURLConnection connection = getConnection(url);
            String contentType = connection.getContentType();
            if(contentType == null) {
                return;
            }
            System.out.println("Page from: " + url);
            boolean isPDF = contentType.equalsIgnoreCase("application/pdf");
            loadNMeasure(connection, url, isPDF);
            if(bandwidthLimiter) {
                recordNSleep();
            }

        } catch(IOException ioe) {
        }
    }

    private HttpURLConnection getConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.addRequestProperty("User-Agent", "Chrome/67.0.3396.99");
        return connection;
    }

    public void loadNMeasure(HttpURLConnection connection, String url, boolean isPDF) {
        long previousTimestamp = System.currentTimeMillis();
        long currentTimestamp = 0;
        CountingInputStream someCountingStream = null;
        InputStream iStream = null;
        try {
            int responseCode = connection.getResponseCode();
            if(responseCode >= 400) {
                return;
            }
            iStream = connection.getInputStream();
            currentTimestamp = System.currentTimeMillis();
            someCountingStream = new CountingInputStream(iStream);
            if(isPDF) {
                pdfstreamer(someCountingStream, pdfstarter(url));
            } else {
                Document htmlDocument = Jsoup.parse(someCountingStream, null, url);
                digest(htmlDocument);
            }
        } catch (IOException e) {
        }
        diffInTimestamps = currentTimestamp - previousTimestamp;
        System.out.println("Time Spent Downloading: " + diffInTimestamps);
        bytesRead = someCountingStream.getCount();
        System.out.println("Bytes Read: " + bytesRead);
        totalBytesRead += bytesRead;
        totalDiffInTimestamps += diffInTimestamps;
        avgBytesPerSec = avgKilobytesPerSecond * 1024;
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

            if(fileOutputClump) {
                FileWriter fw = new FileWriter("Content_Gathered_By_Critterer.txt",true);
                BufferedWriter bw = new BufferedWriter(fw);
                pw = new PrintWriter(bw);
            } else {
                pw = new PrintWriter((titles.text()) + ".txt");
            }

            for (Element title : titles) {
                pw.println(title.text()); //print the title
                //pw.write('\n');
                //System.out.println(title.text());
                //System.out.println("Splittttttttttttttttttttttttttt");
                //System.out.println(title.text().getBytes());
            }

            String html = "<html><head></head>" + "<body><p>" + "</p></body></html>";
            Jsoup.parse(html, "UTF-8");
            Elements paragraphs = htmlDocument.select("body");
            for (Element p : paragraphs) {
                pw.println(p.text());
                //pw.write('\n');
                //System.out.println(p.text());
                //System.out.println("Splittttttttttttttttttttttttttt AGAIIIIIIIIIIIIINNNNNNNNNNNNN");
                //System.out.println(p.text().getBytes());
            }
            //Collects links and adds them to list while counting number found
            pw.flush();
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
            System.out.println("SOMETHING IS WRONG");
        } catch (IOException pwisannoying) {
           System.out.println("SOMETHING ISN'T WORKINGGGgggggggggggggggggggggggg");
        }

        return false;
    }

    /* public void digestClump(Document htmlDocument) {

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
    */

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

    public List<String> getLinks() {
        return this.links;
    }
}
