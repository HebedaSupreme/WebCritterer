package com.webcritterer;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Critterer {

    Critterer(String args[]) {
        this.arguments = args;
        this.maxPagesGoingTo = args[1];
        this.urlFile = args[0];
        maximumPagesToGoTo = Long.parseLong(maxPagesGoingTo);
    }

    public String[] arguments;
    public String maxPagesGoingTo;
    public String urlFile;
    public long maximumPagesToGoTo; //ASSIGN THE MAXIMUM NUMBER OF PAGES TO TRAVEL TO HERE
    public Set<String> pagesAlreadyHit = new HashSet<String>();
    public LinkedList<String> pagesNeededToGoTo = new LinkedList<String>();
    public LinkedList<String> originalURLs = new LinkedList<String>();
    public long totalKilos;
    public CrittererEat scramble;
    public String errorMsg = "Error: Please Refer to ReadMe/Instructions";
    public String usageMsg = "Usage: run [SeedURL |OR| Directory Path to File with URL] <Number of Pages Maximum to Critter> [Number of Kilobytes Per Second Crawling Should Average --> 'No' if not needed]";
    public LinkedList<String> originalDomains = new LinkedList<String>();
    public String nextUrlScrambled;
    public boolean domainRestricter;


    public void addingurllist() {
        domainRestricter = Boolean.parseBoolean(arguments[3]);
        if (arguments[0].contains("txt")) {
            File urlFile = new File(arguments[0]);
            Scanner input = null;
            try {
                input = new Scanner(urlFile);
            } catch (FileNotFoundException e) {
                System.out.println(errorMsg);
                System.out.println(usageMsg);
            }

            while (input.hasNextLine()) {
                String theNextURL = input.nextLine();
                pagesNeededToGoTo.add(theNextURL);
                if(domainRestricter) {
                    URI uri;
                    try {
                        uri = new URI(theNextURL);
                        String hostname = uri.getHost();
                        if (hostname != null) {
                            originalDomains.add(hostname.startsWith("www.") ? hostname.substring(4) : hostname);
                        } else {
                            throw new URISyntaxException(theNextURL, "bad domain name");
                        }
                    } catch (URISyntaxException e) {
                        // print message about format
                        // System exit
                        System.out.println(e.getMessage());
                        System.exit(1);
                    }
                }
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
            if(domainRestricter) {
                LinkedList<String> scrambledLinks = new LinkedList<String>();
                scrambledLinks.addAll(scramble.getLinks());
                //System.out.println("Scrambled links");
                for (String link : scrambledLinks) {
                    System.out.println(link);
                }

                while(!scrambledLinks.isEmpty()) {
                    nextUrlScrambled = scrambledLinks.remove(0);
                    if(domainCompare(nextUrlScrambled)) {
                        System.out.format("%s matched %n", nextUrlScrambled);
                        this.pagesNeededToGoTo.add(nextUrlScrambled);
                    } else {
                        System.out.format("%s did not match %n", nextUrlScrambled);
                    }
                }
            } else {
                this.pagesNeededToGoTo.addAll(scramble.getLinks());
            }
        }
        printingFinalStats();
    }

    public boolean domainCompare(String nextUrlScrambled) {
        try {
            for (String anyOfOGDomains : originalDomains) {
                String[] splitDomain = nextUrlScrambled.split("/");
                String actualSplitDomain = splitDomain[2];
                if (actualSplitDomain.contains(anyOfOGDomains)) {
                    return true;
                }
            }
        } catch(ArrayIndexOutOfBoundsException ar) {
        }
        return false;
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
        System.out.println("**Done** Visited " + this.pagesAlreadyHit.size() + " web page(s)");
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
        long totalDigestTime = scramble.getTotalDigestTime();
        System.out.println("Total Time Spent Processing and Writing: " + totalDigestTime + " millis");
    }


    public long gettotalKilos() {
        return totalKilos;
    }

    public Set<String> getPagesAlreadyHit() {
        return pagesAlreadyHit;
    }

    public List<String> getPagesNeededToGoTo() {
        return pagesNeededToGoTo;
    }

}