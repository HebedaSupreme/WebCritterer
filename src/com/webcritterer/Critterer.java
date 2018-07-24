package com.webcritterer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Critterer {

    Critterer(String args[]){
        this.arguments = args;
    }

    public String[] arguments;
    public String maxPagesGoingTo = arguments[1];
    public long maximumPagesToGoTo = Long.parseLong(maxPagesGoingTo); //ASSIGN THE MAXIMUM NUMBER OF PAGES TO TRAVEL TO HERE
    public Set<String> pagesAlreadyHit = new HashSet<String>();
    public List<String> pagesNeededToGoTo = new LinkedList<String>();
    public long totalKilos;

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
        CrittererEat scramble = new CrittererEat(arguments);
        while (this.pagesAlreadyHit.size() < maximumPagesToGoTo) {
            if (this.pagesNeededToGoTo.isEmpty()) {
                currentUrl = url;
                this.pagesAlreadyHit.add(url);
            } else {
                currentUrl = this.nextUrl();
            }
            scramble.critter(currentUrl);
            this.pagesNeededToGoTo.addAll(scramble.getLinks());
        }
        System.out.println("\n**Done** Visited " + this.pagesAlreadyHit.size() + " web page(s)");
        long totalBytesRead = scramble.gettotalBytesRead();
        totalKilos = totalBytesRead / 1024;
        System.out.println("TotalKilos: " + totalKilos);
        long totalDiffTime = scramble.gettotalDiffInTimestamps();
        long totalTimeDownloadingSec = totalDiffTime / 1000;
        long totalTimeSlept = scramble.gettotalSleepTime();
        long totalSleepSec = totalTimeSlept / 1000;
        float averageBytesPerTime = (totalKilos) / ((float) totalSleepSec + totalTimeDownloadingSec);
        float totalTruncatedValue = scramble.gettotalTruncatedValue();
        long maxBytesReadAtOnce = scramble.getmaxBytesReadAtOnce();
        long maxKilosReadAtOnce = maxBytesReadAtOnce / 1024;
        float totalTheoryTime = scramble.gettotalTheoryTime();
        System.out.println("Average KB/sec by Addition: " + averageBytesPerTime + " kilobytes/sec");
        System.out.println("Time slept: " + totalTimeSlept + " milliseconds");
        System.out.println("Time Theoretically Supposed To Be Slept: " + totalTheoryTime);
        System.out.println("Time Truncated: " + totalTruncatedValue + " millis");
        System.out.println("Maximum Kilobytes/Sec: " + maxKilosReadAtOnce + " kilobytes/sec");
        float totalTimeRunningByAddition = (float) totalTimeDownloadingSec + totalSleepSec;
        System.out.println("Total Time Spent Running By Addition of Sleeping and Downloading: " + totalTimeRunningByAddition + " seconds");

    }

    public long gettotalKilos() {
        return totalKilos;
    }

}