package com.webcritterer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Critterer {
    public static final long maximumPagesToGoTo = 50; //ASSIGN THE MAXIMUM NUMBER OF PAGES TO TRAVEL TO HERE
    private Set<String> pagesAlreadyHit = new HashSet<String>();
    private List<String> pagesNeededToGoTo = new LinkedList<String>();
    public long totalKilos;
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
        while (this.pagesAlreadyHit.size() < maximumPagesToGoTo) { //while pagesalreadyhit is under limit
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
        long totalBytesRead = scramble.gettotalBytesRead();
        totalKilos = totalBytesRead/1024;
        System.out.println("TotalKilos: " + totalKilos);
        long totalDiffTime = scramble.gettotalDiffInTimestamps();
        long totalTimeDownloadingSec = totalDiffTime/1000;
        long totalTimeSlept = scramble.gettotalSleepTime();
        long totalSleepSec = totalTimeSlept/1000;
        float averageBytesPerTime = (totalKilos) / ((float)totalSleepSec + totalTimeDownloadingSec);
        float totalTruncatedValue = scramble.gettotalTruncatedValue();
        long maxBytesReadAtOnce = scramble.getmaxBytesReadAtOnce();
        long maxKilosReadAtOnce = maxBytesReadAtOnce/1024;
        float totalTheoryTime = scramble.gettotalTheoryTime();
        //float avgByTimestamp = totalKilos/totalTimeRunnning;
        System.out.println("Average KB/sec by Addition: " + averageBytesPerTime + " kilobytes/sec");
        //System.out.println("Average KB/sec by Timestamp: " + avgByTimestamp + " kilobytes/sec");
        System.out.println("Time slept: " + totalTimeSlept + " milliseconds");
        System.out.println("Time Theoretically Supposed To Be Slept: " + totalTheoryTime);
        System.out.println("Time Truncated: " + totalTruncatedValue + " millis");
        System.out.println("Maximum Kilobytes/Sec: " + maxKilosReadAtOnce + " kilobytes/sec");
        float totalTimeRunningByAddition = (float) totalTimeDownloadingSec + totalSleepSec;
        //System.out.println("Total Time Spent Running: " + totalTimeRunnning + " seconds");
        System.out.println("Total Time Spent Running By Addition of Sleeping and Downloading: " + totalTimeRunningByAddition + " seconds");

    }

    public long gettotalKilos(){
        return totalKilos;
    }

}