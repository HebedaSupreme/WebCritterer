package com.webcritterer;

import java.util.List;
import java.util.Set;

public class CrittererLaunchpad {

    //0...url or file file   1...pages 2...bandwidthlimiter 3...domainboundary 4...fileoutputclump

    public String[] args;
    public String errorMsg = "Error: Please Refer to ReadMe/Instructions";
    public String usageMsg = "Usage: ./run.sh <Seed URL or Text File Containing URLs> {(Optional in any order ) [--numbersofpagestoload=NUMBER of pages to download] [--bandwidthlimit=NUMBER average of KB/sec to critter at] [--stayindomain] [--dumpinsinglefile]}";
    public String otherSpecsMsg = "If other specifications are preferred, please refer to ReadMe/Instructions";
    Critterer critterer;

    CrittererLaunchpad(String args[]) {
        this.args = args;
    }

    public void errorMessaging() {
        System.out.println(errorMsg);
        System.out.println(usageMsg);
    }

    public void askSpecsMessaging() {
        System.out.println(otherSpecsMsg);
        System.out.println(usageMsg);
    }

    public void launchpad() {
        if (args.length == 0 || args[0].equals("help")) {
            System.out.println(usageMsg);
            return;
        }
        System.out.println("Running Configuration On " + args[0]);
        askSpecsMessaging();
        long maxPagesGoingTo = 0;
        boolean pagesLimiter = false;
        boolean bandwidthLimiter = false;
        boolean domainRestricter = false;
        boolean fileOutputClump = false;
        long bandwidthLimitValue = 0;

        for (int argNum = 1; argNum < args.length; argNum++) {

            if (args[argNum].startsWith("--maxpages=")) {
                String[] pagesToLoadInput = args[argNum].split("=");
                String pagesToLoadString = pagesToLoadInput[1];
                maxPagesGoingTo = Long.parseLong(pagesToLoadString);
                System.out.println("Downloading " + maxPagesGoingTo + " pages");
                pagesLimiter = true;

            } else if (args[argNum].startsWith("--maxbandwidth=")) {
                String[] bandwidthLimitInput = args[argNum].split("=");
                String bandwidthLimitValueInput = bandwidthLimitInput[1];
                bandwidthLimitValue = Long.parseLong(bandwidthLimitValueInput);
                bandwidthLimiter = true;
                System.out.println("Bandwidth Limit On At: " + bandwidthLimitValue + " KB/sec");

            } else if (args[argNum].equals("--domainstay")) {
                domainRestricter = true;
                System.out.println("Critterering Restricted to Seed Domain");

            } else if (args[argNum].equals("--onefile")) {
                fileOutputClump = true;
                System.out.println("Content Critterered Will Be Placed In Single Text File");

            } else {
                System.out.println(args[argNum] + " has been formatted incorrectly");
                errorMessaging();
                return;
            }

        }
        launchup(bandwidthLimitValue, domainRestricter, fileOutputClump, bandwidthLimiter, maxPagesGoingTo, pagesLimiter);
    }

    public void launchup(long bandwidthLimitValue, boolean domainRestricter, boolean fileOutputClump, boolean bandwidthLimiter, long maxPagesGoingTo, boolean pagesLimiter) {
        long startTimestamp = System.currentTimeMillis();
        critterer = new Critterer(args, bandwidthLimitValue, domainRestricter, fileOutputClump, bandwidthLimiter, maxPagesGoingTo, pagesLimiter);
        critterer.addingurllist();
        critterer.loader();
        long endTimestamp = System.currentTimeMillis();
        if (pagesLimiter) {
            printMoreFinalStats(startTimestamp, endTimestamp);
        }
    }


    public void printMoreFinalStats(long startTimestamp, long endTimestamp) {
        float totalTimeRunnning = (endTimestamp - startTimestamp) / 1000;
        System.out.println("Total Time Running By Timestamp: " + totalTimeRunnning + " seconds");
        long totalKilos = critterer.gettotalKilos();
        float avgByTimestamp = totalKilos / totalTimeRunnning;
        System.out.println("Average KB/sec by Timestamp: " + avgByTimestamp + " kilobytes/sec");
    }

}