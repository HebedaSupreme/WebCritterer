package com.webcritterer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class CrittererEat

{
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;
    //public String pathToDir = "~/Users/hebeda_supreme/Desktop/";


    public List<String> getLinks() {
        return this.links;
    }

    public boolean critter(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            InputStream iStream = connection.getInputStream();
            CrittererBandwidthLimitation in = new CrittererBandwidthLimitation(iStream);
            String htmlText = org.apache.commons.io.IOUtils.toString(in, connection.getContentEncoding());
            Document htmlDocument = Jsoup.parse(htmlText);
            this.htmlDocument = htmlDocument;
            String filename = "<title></title>";
            Jsoup.parse(filename);
            Elements titles = htmlDocument.select("title");
            PrintStream printStream = new PrintStream((/*pathToDir +*/ titles.text()) + ".txt");
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
            return true;
        } catch (IOException ioe) {
            return false;
        }

    }
}


