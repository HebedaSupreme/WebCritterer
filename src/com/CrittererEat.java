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
            URLConnection connection = new URL(url).openConnection(); //create a URL connection to the URL
            InputStream iStream = connection.getInputStream(); //Gets inputstream from the connection
            CrittererBandwidthLimitation in = new CrittererBandwidthLimitation(iStream); //Creates a new Bandwidth limiter which will inherit the inputstream
            String htmlText = org.apache.commons.io.IOUtils.toString(in, connection.getContentEncoding()); //The inputstream having returned from the limiter, will be taken as a string
            Document htmlDocument = Jsoup.parse(htmlText); //jsoup package parses the string
            this.htmlDocument = htmlDocument;
            //Will parse content from <title> headers and make them titles of printed text documents
            String filename = "<title></title>";
            Jsoup.parse(filename);
            Elements titles = htmlDocument.select("title");
            PrintStream printStream = new PrintStream((/*pathToDir +*/ titles.text()) + ".txt");
            //Set the printstream out to a text file
            System.setOut(printStream);
            System.out.println("From page at:" + url); //print a message

            for (Element title : titles)
                System.out.println(title.text()); //print the title

            //Parses the content from the page and prints it into text file
            String html = "<html><head></head>" + "<body><p>" + "</p></body></html>";
            Jsoup.parse(html);
            Elements paragraphs = htmlDocument.select("p");
            for (Element p : paragraphs)
                System.out.println(p.text());
            //Collects links and adds them to list while counting number found
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


