package com.webcritterer;

import java.util.List;
import java.util.Set;

public class CrittererLaunchpad {

    //0...url or file file   1...pages 2...bandwidthlimiter 3...domainboundary 4...fileoutputclump

    public String[] args;
    public String errorMsg = "Error: Please Refer to ReadMe/Instructions";
    public String usageMsg ="./run.sh <Seed URL or Text File Containing URLs> <NUMBER of pages to critter> { (Optional in any order ) [--bandwidthlimit=NUMBER average of KB/sec to critter at] [--stayindomain] [--dumpinsinglefile]}";
    public String otherSpecsMsg = "If other specifications are preferred, please refer to ReadMe/Instructions";
    Critterer critterer;
    long totalTimeRunnning;
    long startTimestamp;
    long endTimestamp;
    long bandwidthLimitValue;
    boolean domainRestricter;
    boolean fileOutputClump;
    boolean bandwidthLimiter;


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
                System.out.println("Running Configuration On Seed URL " + args[0] + " For " + args[1] + " Webpages");
                for (int argNum = 2; argNum < args.length; argNum++) {

                    if (args[argNum].startsWith("--bandwidthlimit=")) {
                        String[] bandwidthLimitInput = args[argNum].split("=");
                        String bandwidthLimitValueInput = bandwidthLimitInput[1];
                        bandwidthLimitValue = Long.parseLong(bandwidthLimitValueInput);
                        bandwidthLimiter = true;
                        System.out.println("Bandwidth Limit On At: " + bandwidthLimitValue + " KB/sec");

                    } else if (args[argNum].equals("--stayindomain")) {
                        domainRestricter = true;
                        System.out.println("Critterering Restricted to Seed Domain");

                    } else if (args[argNum].equals("--dumpinsinglefile")) {
                        fileOutputClump = true;
                        System.out.println("Content Critterered Will Be Placed In Single Text File");

                    }

                }

        launchup();
        askSpecsMessaging();
    }


    public void launchup() {
        try {
            startTimestamp = System.currentTimeMillis();
            critterer = new Critterer(args, bandwidthLimitValue, domainRestricter, fileOutputClump, bandwidthLimiter);
            critterer.addingurllist();
            critterer.load(args[0]);
            endTimestamp = System.currentTimeMillis();
            printMoreFinalStats();
        } catch(ArrayIndexOutOfBoundsException errorinput) {
            errorMessaging();
        }
    }


    public void printMoreFinalStats() {
        totalTimeRunnning = (endTimestamp - startTimestamp) / 1000;
        System.out.println("Total Time Running By Timestamp: " + totalTimeRunnning + " seconds");
        long totalKilos = critterer.gettotalKilos();
        long avgByTimestamp = totalKilos / totalTimeRunnning;
        System.out.println("Average KB/sec by Timestamp: " + avgByTimestamp + " kilobytes/sec");
    }

}