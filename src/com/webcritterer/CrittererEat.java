package com.webcritterer;

import org.apache.commons.io.input.CountingInputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class CrittererEat {
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;
    private String url;
    long maxKilobytesPerSecond = 100;
    private InputStream inputStream;
    long previousTimestamp = System.currentTimeMillis();
    long counter = 0;
    int second = 1000;
    long maxbytespersec = maxKilobytesPerSecond * 1000;


    public List<String> getLinks() {
        return this.links;
    }

    public String critter(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            InputStream iStream = connection.getInputStream();
            CountingInputStream someCountingstream = new CountingInputStream(iStream);
            String htmlText = org.apache.commons.io.IOUtils.toString(someCountingstream);
            int bytesRead = someCountingstream.getCount();
            if (bytesRead >= maxbytespersec) { //if bytes counted exceeds the first increment
                long currentTimestamp = System.currentTimeMillis(); //create timestamp
                if (previousTimestamp + second >= currentTimestamp) { //verify the bytes were read within a second-long interval
                    Thread.sleep(second); //if bytes per second exceeded limit sleep for three seconds
                }
                previousTimestamp = currentTimestamp;
            }
            return process(htmlText);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String process (String htmlText ) {
        try {
            Document htmlDocument = Jsoup.parse(htmlText);
            this.htmlDocument = htmlDocument;
            String filename = "<title></title>";
            Jsoup.parse(filename);
            Elements titles = htmlDocument.select("title");
            PrintStream printStream = new PrintStream((titles.text()) + ".txt");
            System.setOut(printStream);
            System.out.println("From page at:" + url);

            for (Element title : titles)
                System.out.println(title.text());

            String html = "<html><head></head>" + "<body><p>" + "</p></body></html>";
            Jsoup.parse(html);
            Elements paragraphs = htmlDocument.select("p");
            for (Element p : paragraphs)
                System.out.println(p.text());
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("**Grabbed (" + linksOnPage.size() + ") links***");
            for (Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));
            }
        } catch(IOException ioe) {

        }
        return null;
    }
}