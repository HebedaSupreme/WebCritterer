package com.webcritterer;

import java.io.IOException;

public class CritteringStarter {

    public static void main(String[] args) {
        long totalTimeRunnning;
        long startTimestamp = System.currentTimeMillis();
        Critterer critterer = new Critterer(); //initializes Critterer class
        critterer.load("http://www.uci.edu"); // INSERT SEED URL here
        long endTimestamp = System.currentTimeMillis();
        totalTimeRunnning = (endTimestamp - startTimestamp) / 1000;
        System.out.println("Total Time Running By Timestamp: " + totalTimeRunnning + " seconds");
        long totalKilos = critterer.gettotalKilos();
        long avgByTimestamp = totalKilos / totalTimeRunnning;
        System.out.println("Average KB/sec by Timestamp: " + avgByTimestamp + " kilobytes/sec");

    }
}
