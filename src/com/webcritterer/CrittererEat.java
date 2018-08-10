package com.webcritterer;

import org.apache.commons.io.input.CountingInputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.print.PrinterAbortException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.io.FileOutputStream;


public class CrittererEat {

    public String[] args;
    private List<String> links = new LinkedList<String>();
    long totalBytesRead;
    long totalDiffInTimestamps;
    long totalSleepTime;
    long maxBytesReadAtOnce;
    float totalTruncatedValue;
    float totalTheoryTime;
    float avgBytesPerSec;
    long totalDigestTime;
    public boolean fileOutputClump;
    public boolean bandwidthLimiter;


    CrittererEat(String[] arguments, long bandwidthLimitValue, boolean fileOutputClump, boolean bandwidthLimiter){
        this.args = arguments;
        if (bandwidthLimiter) {
            avgBytesPerSec = bandwidthLimitValue * 1024;
        }
        this.bandwidthLimiter = bandwidthLimiter;
        this.fileOutputClump = fileOutputClump;
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
        long diffInTimestamps = currentTimestamp - previousTimestamp;
        timestampTotality(diffInTimestamps);
        long bytesRead = someCountingStream.getCount();
        bytesTotality(bytesRead);
        maxBytesRecorder(bytesRead);
        if(bandwidthLimiter) {
            theoryNReal(diffInTimestamps, bytesRead);
        }
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

    public void timestampTotality(long diffInTimestamps) {
        System.out.println("Time Spent Downloading: " + diffInTimestamps);
        totalDiffInTimestamps += diffInTimestamps;
    }

    public void bytesTotality(long bytesRead) {
        System.out.println("Bytes Read: " + bytesRead);
        totalBytesRead += bytesRead;
    }

    public void theoryNReal(long diffInTimestamps, long bytesRead) {
        float theoreticalSleepTime = (((bytesRead * 1000) / avgBytesPerSec) - diffInTimestamps);
        long neededSleepTime = (long) (theoreticalSleepTime);
        float truncatedValue = (theoreticalSleepTime - neededSleepTime);
        truncatedTotality(truncatedValue);
        sleeperCell(neededSleepTime);
        theoryTotality(theoreticalSleepTime);
    }

    public void theoryTotality(float theoreticalSleepTime) {
        if (theoreticalSleepTime > 0) {
            System.out.println("Theoretical Sleep Time: " + theoreticalSleepTime);
            totalTheoryTime += theoreticalSleepTime;
        }
    }

    public void sleeperCell(long neededSleepTime) {
        if (neededSleepTime > 0) {
            totalSleepTime += neededSleepTime;
            System.out.println("neededSleepTime" + neededSleepTime);
            try {
                Thread.sleep(neededSleepTime);
            } catch (InterruptedException e) {
            }
        }
    }

    public void truncatedTotality(float truncatedValue) {
        if (truncatedValue > 0) {
            System.out.println("Truncated Time: " + truncatedValue);
            totalTruncatedValue += truncatedValue;
        }
    }

    public void maxBytesRecorder(long bytesRead) {
        if (bytesRead > maxBytesReadAtOnce) {
            maxBytesReadAtOnce = bytesRead;
        }
    }

    public void digest(Document htmlDocument) {
        try {
            long digestTimestamp = System.currentTimeMillis();
            Elements titles = getTitle(htmlDocument);
            PrintWriter pw = null;
            if (fileOutputClump) {
                FileWriter fw = new FileWriter("Content_Gathered_By_Critterer.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                pw = new PrintWriter(bw);
            } else {
                pw = new PrintWriter((titles.text()) + ".txt");
            }
            printTitle(titles, pw, htmlDocument);
            pw.flush();
            pw.close();
            digestTimestamps(digestTimestamp);
        } catch (FileNotFoundException e) {
        } catch (IOException pwisannoying) {
        }

    }

    public Elements getTitle(Document htmlDocument) {
        String filename = "<title></title>";
        Jsoup.parse(filename, "UTF-8");
        Elements titles = htmlDocument.select("title");
        return titles;
    }

    public void printTitle(Elements titles, PrintWriter pw, Document htmlDocument) {
        for (Element title : titles) {
            pw.println(title.text());
            printpg(htmlDocument, pw);
        }
    }

    public void printpg(Document htmlDocument, PrintWriter pw) {
        String html = "<html><head></head>" + "<body><p>" + "</p></body></html>";
        Jsoup.parse(html, "UTF-8");
        Elements paragraphs = htmlDocument.select("body");
        for (Element p : paragraphs) {
            pw.println(p.text());
        }
        grabLinks(htmlDocument);
    }

    public void digestTimestamps(long digestTimestamp) {
        long digestEndTimestamp = System.currentTimeMillis();
        long digestTime = digestEndTimestamp - digestTimestamp;
        totalDigestTime += digestTime;
    }

    public void grabLinks(Document htmlDocument) {
        Elements linksOnPage = htmlDocument.select("a[href]");
        System.out.println("**Grabbed (" + linksOnPage.size() + ") links***");
        for (Element link : linksOnPage) {
            this.links.add(link.absUrl("href"));
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

    public List<String> getLinks() {
        return this.links;
    }
}
