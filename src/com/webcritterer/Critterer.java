package com.webcritterer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Critterer {

    public String[] arguments;
    public String maxPagesGoingTo;
    public String urlFile;
    public long maximumPagesToGoTo;
    public Set<String> pagesAlreadyHit = new HashSet<String>();
    public LinkedList<String> pagesNeededToGoTo = new LinkedList<String>();
    public long totalKilos;
    public CrittererEat scramble;
    public String errorMsg = "Error: Please Refer to ReadMe/Instructions";
    public String usageMsg = "./run.sh <Seed URL or Text File Containing URLs> {(Optional in any order ) [--numbersofpagestoload=NUMBER of pages to download] [--bandwidthlimit=NUMBER average of KB/sec to critter at] [--stayindomain] [--dumpinsinglefile]}";
    public LinkedList<String> originalDomains = new LinkedList<String>();
    public String nextUrlScrambled;
    public boolean domainRestricter;
    public boolean fileOutputClump;
    public long bandwidthLimitValue;
    public boolean bandwidthLimiter;


    Critterer(String args[], long bandwidthLimitValue, boolean domainRestricter, boolean fileOutputClump, boolean bandwidthLimiter, long maximumPagesToGoTo) {
        try {
            this.arguments = args;
            this.urlFile = args[0];
            this.maximumPagesToGoTo = maximumPagesToGoTo;
            this.fileOutputClump = fileOutputClump;
            this.domainRestricter = domainRestricter;
            this.bandwidthLimitValue = bandwidthLimitValue;
            this.bandwidthLimiter = bandwidthLimiter;
        } catch(NumberFormatException errorinput) {
            System.out.println(errorMsg);
            System.out.println(usageMsg);
        }
    }


    public void addingurllist() {
        if (arguments[0].contains("https://")) {
            if(domainRestricter) {
                ogDomainsExtractor(arguments[0]);
            }
        }
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
                    ogDomainsExtractor(theNextURL);
                }
            }
        }
    }

    public void ogDomainsExtractor(String anOGURL) {
        URI uri;
        try {
            uri = new URI(anOGURL);
            String hostname = uri.getHost();
            if (hostname != null) {
                originalDomains.add(hostname.startsWith("//") ?  hostname.substring(1) : hostname);
                System.out.println(originalDomains);
            } else {
                throw new URISyntaxException(anOGURL, "bad domain name");
            }
        } catch (URISyntaxException e) {
            // print message about format
            // System exit
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void load(String url) {
        String currentUrl;
        scramble = new CrittererEat(arguments, bandwidthLimitValue, fileOutputClump, bandwidthLimiter);
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
                //for (String link : scrambledLinks) {
                //System.out.println(link);
                //}

                while(!scrambledLinks.isEmpty()) {
                    nextUrlScrambled = scrambledLinks.remove(0);
                    if(domainCompare(nextUrlScrambled)) {
                        //System.out.format("%s matched %n", nextUrlScrambled);
                        this.pagesNeededToGoTo.add(nextUrlScrambled);
                    } /* else {
                        System.out.format("%s did not match %n", nextUrlScrambled);
                    } */
                }
            } else {
                this.pagesNeededToGoTo.addAll(scramble.getLinks());
            }

            if(fileOutputClump) {
                clumpNDump();
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

    public void clumpNDump() {
        try {
            PrintWriter pw = new PrintWriter("Content_Crawled_By_Critterer.txt");
            pw.println(scramble.getArticlesClump());
            pw.close();
        } catch (FileNotFoundException e) {
        }

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