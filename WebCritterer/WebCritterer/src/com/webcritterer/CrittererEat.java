package com.webcritterer;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public class CrittererEat

{
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;


    public List<String> getLinks() {
        return this.links;
    }

    public boolean critter(String url) {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            if (connection.response().statusCode() == 200) {
                String filename= "<title></title>";
                Jsoup.parse(filename);
                Elements titles = htmlDocument.select("title");
                PrintStream o = new PrintStream((titles.text())+".txt");
                System.setOut(o);
                System.out.println("From page at:" + url);
                for
                        (Element title : titles)
                        System.out.println(title.text());

                String html= "<html><head></head>" + "<body><p>"+"</p></body></html>";
                Jsoup.parse(html);
                Elements paragraphs = htmlDocument.select("p");
                for
                (Element p : paragraphs)
                    System.out.println(p.text());
                Elements linksOnPage = htmlDocument.select("a[href]");
                System.out.println("**Grabbed (" + linksOnPage.size() + ") links***");
                for (Element link : linksOnPage) {
                    this.links.add(link.absUrl("href"));
                }
            }
            if (!connection.response().contentType().contains("text/html")) {
               System.out.println("Error: You sure this stuff is html?");
                return false;
           }
            return true;
        } catch (IOException ioe) {
            return false;
        }

    }
}


