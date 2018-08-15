package com.webcritterer;

import java.util.LinkedList;

public class CrittererLaunchpad {

    //0...url or file file   1...pages 2...bandwidthlimiter 3...domainboundary 4...fileoutputclump

    public String[] args;
    public String usageMsg = "./run.sh {List all URLS here} [(Optional in any order) <--seedtxt=Directory Path to Text File Containing URLS> <--maxpages=Maximum NUMBER of pages to crawl to> <--maxbandwidth=Max NUMBER of Kilobytes/Second> <--domainstay> <--onefile>]";
    Critterer critterer;

    CrittererLaunchpad(String args[]) {
        this.args = args;
    }

    public void launchpad() {
        if (args.length == 0 || args[0].equals("--help")) {
            System.out.println("Usage: " + usageMsg);
            return;
        }
        long maxPagesGoingTo = 0;
        boolean pagesLimiter = false;
        boolean bandwidthLimiter = false;
        boolean domainRestricter = false;
        boolean fileOutputClump = false;
        boolean seedList = false;
        long bandwidthLimitValue = 0;
        String seedListPass = new String();
        LinkedList<String> listedURLs = new LinkedList<String>();

        for (int argNum = 0; argNum < args.length; argNum++) {

            if (args[argNum].startsWith("--maxpages=")) {
                pagesLimiter = true;
                maxPagesGoingTo = getMaxPages(args[argNum]);

            } else if (args[argNum].startsWith("--seedtxt=")) {
                seedList = true;
                seedListPass = getSeedTxt(args[argNum]);

            } else if (args[argNum].startsWith("--maxbandwidth=")) {
                bandwidthLimiter = true;
                bandwidthLimitValue = getMaxBandwidth(args[argNum]);

            } else if (args[argNum].equals("--domainstay")) {
                domainRestricter = true;
                //System.out.println("Critterering Restricted to Seed Domain");

            } else if (args[argNum].equals("--onefile")) {
                fileOutputClump = true;
                //System.out.println("Content Critterered Will Be Placed In Single Text File");

            } else if (args[argNum].contains("http://") || args[argNum].contains("https://")) {
                listedURLs.add(args[argNum]);

            } else {
                System.out.println("Unrecognized argument/input: " + args[argNum]);
                return;
            }
        }
        launchup(bandwidthLimitValue, domainRestricter, fileOutputClump, bandwidthLimiter, maxPagesGoingTo, pagesLimiter, seedList, seedListPass, listedURLs);
    }

    public void launchup(long bandwidthLimitValue, boolean domainRestricter, boolean fileOutputClump, boolean bandwidthLimiter, long maxPagesGoingTo, boolean pagesLimiter, boolean seedList, String seedListPath, LinkedList<String> listedURLs) {
        long startTimestamp = System.currentTimeMillis();
        critterer = new Critterer(bandwidthLimitValue, domainRestricter, fileOutputClump, bandwidthLimiter, maxPagesGoingTo, pagesLimiter, seedList, seedListPath, listedURLs);
        critterer.addingurllist();
        critterer.loader();
        long endTimestamp = System.currentTimeMillis();
        if (pagesLimiter) {
            printMoreFinalStats(startTimestamp, endTimestamp);
        }
    }


    public void printMoreFinalStats(long startTimestamp, long endTimestamp) {
        float totalTimeRunnning = (endTimestamp - startTimestamp) / 1000;
        System.out.println("Finished in: " + totalTimeRunnning + " seconds");
    }

    private long getMaxPages(String arg) {
        String[] pagesToLoadInput = arg.split("=");
        if (pagesToLoadInput.length != 2) {
            throw new IllegalArgumentException("Expected long after --maxpages=");
        }
        String pagesToLoadString = pagesToLoadInput[1];
        long result = 0;
        try {
            result = Long.parseLong(pagesToLoadString);
        } catch (NumberFormatException longnotlong) {
            throw new IllegalArgumentException("Expected long after --maxpages=");
        }
        //System.out.println("Downloading " + result + " pages");
        return result;
    }

    private long getMaxBandwidth(String arg) {
        String[] bandwidthLimitInput = arg.split("=");
        if (bandwidthLimitInput.length != 2) {
            throw new IllegalArgumentException("Expected long after --maxbandwdith=");
        }
        String bandwidthLimitValueInput = bandwidthLimitInput[1];
        long result = 0;
        try {
            result = Long.parseLong(bandwidthLimitValueInput);
        } catch (NumberFormatException longnotlong) {
            throw new IllegalArgumentException("Expected long after --maxbandwidth=");
        }
        //System.out.println("Bandwidth Limit On At: " + result + " KB/sec");
        return result;
    }

    private String getSeedTxt(String arg) {
        String[] seedListPathInput = arg.split("=");
        if (seedListPathInput.length != 2) {
            throw new IllegalArgumentException("Expected file name after --seedtxt=");
        }
        String result = seedListPathInput[1];
        //System.out.println("Using URL list from " + result);
        return result;
    }
}
