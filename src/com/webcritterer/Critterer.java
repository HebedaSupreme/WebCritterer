package com.webcritterer;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Critterer {
    public static final long MaximumPagesToGoTo = 100; //ASSIGN THE MAXIMUM NUMBER OF PAGES TO TRAVEL TO HERE
    private Set<String> pagesAlreadyHit = new HashSet<String>();
    private List<String> pagesNeededToGoTo = new LinkedList<String>();
    private long totalbytesread = 0;
    private long totaltimedownloading = 0;
    //created a list of pages to go to and a set of pages already hit (set because of unique entries)

    //get first entry from pagesNeededToGoTo, make sure URL hasn't already been visited, or find the next one to visit
    private String nextUrl() {
        String nextUrl;
        do {
            nextUrl = this.pagesNeededToGoTo.remove(0);
        } while (this.pagesAlreadyHit.contains(nextUrl));
        this.pagesAlreadyHit.add(nextUrl);
        return nextUrl;
    }

    public void load(String url) {
        String currentUrl;
        CrittererEat scramble = new CrittererEat(); //initializes CrittererEat which will travel through a connection and parse the content
        while (this.pagesAlreadyHit.size() < MaximumPagesToGoTo) { //while pagesalreadyhit is under limit
            if (this.pagesNeededToGoTo.isEmpty()) {
                currentUrl = url;   //if pages to go to is empty, the currentURL is the url to travel to, and add said url to list of those already hit
                this.pagesAlreadyHit.add(url);
            } else {  //CHANGE THIS SO URL AUTOMATICALLY ADDED TO ALREADYHIT SO THAT "else" CAN BE PRIMARY
                currentUrl = this.nextUrl(); //if list not empty, currentURL will be next
            }
            scramble.critter(currentUrl); //method where CrittererEat is called to connect to and parse URL
            this.pagesNeededToGoTo.addAll(scramble.getLinks()); //collect more URLs
        }
        System.out.println("\n**Done** Visited " + this.pagesAlreadyHit.size() + " web page(s)"); //print message on text file
        long totalbytesread = scramble.gettotalbytesRead();
        long totalkilos = totalbytesread/1024;
        long totaldifftime = scramble.gettotaldiffinTimestamps();
        long totaltimedownloadingsec = totaldifftime/1000;
        long totaltimeslept = scramble.gettotalsleeptime();
        long totalsleepsec = totaltimeslept/1000;
        float averagebytespertime = ((float) totalkilos)/ (totalsleepsec + totaltimedownloadingsec);
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        System.out.println(averagebytespertime + " kilobytes/sec");
        System.out.println("Time slept:" + totaltimeslept + " milliseconds");
    }

}