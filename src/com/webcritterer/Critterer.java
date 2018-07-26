package com.webcritterer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Critterer {

    Critterer(String args[]){
        this.arguments = args;
        this.maxPagesGoingTo = args[1];
        this.urlFile = args[0];
        maximumPagesToGoTo =  Long.parseLong(maxPagesGoingTo);
    }

    public String[] arguments;
    public String maxPagesGoingTo;
    public String urlFile;
    public long maximumPagesToGoTo; //ASSIGN THE MAXIMUM NUMBER OF PAGES TO TRAVEL TO HERE
    public Set<String> pagesAlreadyHit = new HashSet<String>();
    public List<String> pagesNeededToGoTo = new LinkedList<String>();
    public long totalKilos;
    public CrittererEat scramble;


    public void addingurllist(){
        if (arguments[0].contains("txt")){
            File urlFile = new File(arguments[0]);
            Scanner input = null;
            try {
                input = new Scanner(urlFile);
            } catch (FileNotFoundException e) {
            }

            while (input.hasNextLine()) {
                String theNextURL = input.nextLine();
                pagesNeededToGoTo.add(theNextURL);
            }
        }
    }

    public void load(String url) {
        String currentUrl;
        scramble = new CrittererEat(arguments);
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
        printingFinalStats();
    }

    private String nextUrl() {
        String nextUrl;
        do {
            nextUrl = this.pagesNeededToGoTo.remove(0);
        } while (this.pagesAlreadyHit.contains(nextUrl));
        this.pagesAlreadyHit.add(nextUrl);
        return nextUrl;
    }

    public void printingFinalStats() {
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
        System.out.println("List of Pages Crittered: ");
        System.out.println(pagesAlreadyHit);
    }


    public long gettotalKilos() {
        return totalKilos;
    }

    public List<String> getPagesNeededToGoTo() {
        return pagesNeededToGoTo;
    }

}