package com.webcritterer;

import java.io.IOException;

public class CritteringStarter {

    public static void main(String[] args) {
        String seedURL = args[0];
        //String maximumPagesToGoTo = args[1];
        //String bandwidthLimiter = args[2];
        //String avgKilobytesPerSecond = args[3];
        long totalTimeRunnning;
        long startTimestamp = System.currentTimeMillis();
        Critterer critterer = new Critterer(args); //initializes Critterer class
        critterer.load("https://" + seedURL); // INSERT SEED URL here
        long endTimestamp = System.currentTimeMillis();
        totalTimeRunnning = (endTimestamp - startTimestamp) / 1000;
        System.out.println("Total Time Running By Timestamp: " + totalTimeRunnning + " seconds");
        long totalKilos = critterer.gettotalKilos();
        long avgByTimestamp = totalKilos / totalTimeRunnning;
        System.out.println("Average KB/sec by Timestamp: " + avgByTimestamp + " kilobytes/sec");

    }
}
