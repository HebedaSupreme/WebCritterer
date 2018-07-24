package com.webcritterer;

import java.io.IOException;
import java.util.Scanner;

public class CritteringStarter {
    //Note that String maximumPagesToGoTo = args[1];
    //Note that String bandwidthLimiter = args[2];
    //Note that String avgKilobytes = args[3];

    public static void main(String[] args) {

        if (args != null && args.length > 0) {

            if (args[0].contains("www.")) {
                String seedURL = args[0];

                if (args[1].matches("[0-9]")) {

                    if (args[2].contains("true") || args[2].contains("false")) {

                        if (args[3].matches("[0-9]")) {
                            long startTimestamp = System.currentTimeMillis();
                            Critterer critterer = new Critterer(args); //initializes Critterer class
                            critterer.load("https://" + seedURL); // INSERT SEED URL here
                            long endTimestamp = System.currentTimeMillis();
                            long totalTimeRunnning;
                            totalTimeRunnning = (endTimestamp - startTimestamp) / 1000;
                            System.out.println("Total Time Running By Timestamp: " + totalTimeRunnning + " seconds");
                            long totalKilos = critterer.gettotalKilos();
                            long avgByTimestamp = totalKilos / totalTimeRunnning;
                            System.out.println("Average KB/sec by Timestamp: " + avgByTimestamp + " kilobytes/sec");

                        } else {
                            System.out.println("Error: The Average Bandwidth Per Second Must Be A Number of Kilobytes Per Second (e.g. 123)");
                            System.out.println("Please Refer to ReadMe/Instructions");
                        }
                    } else {
                        System.out.println("Error: Please Indicate Whether Bandwidth Should Be Limited Using Only Either 'true' or 'false'");
                        System.out.println("Please Refer to ReadMe/Instructions");
                    }
                } else {
                    System.out.println("Running Default Configuration of 200 Pages With No Bandwidth Limit On Seed URL " + args[0]);
                    System.out.println("If other specifications are preferred, please refer to ReadMe/Instructions");
                    args[1] = "200";
                    args[2] = "false";
                    long startTimestamp = System.currentTimeMillis();
                    Critterer critterer = new Critterer(args);
                    critterer.load("https://" + args[0]);
                    long endTimestamp = System.currentTimeMillis();
                    long totalTimeRunnning;
                    totalTimeRunnning = (endTimestamp - startTimestamp) / 1000;
                    System.out.println("Total Time Running By Timestamp: " + totalTimeRunnning + " seconds");
                    long totalKilos = critterer.gettotalKilos();
                    long avgByTimestamp = totalKilos / totalTimeRunnning;
                    System.out.println("Average KB/sec by Timestamp: " + avgByTimestamp + " kilobytes/sec");
                }
            } else {
                System.out.println("Error: Please Enter A Valid URL (start with www.)");
                System.out.println("Please Refer to ReadMe/Instructions");
            }
        } else {
            System.out.println("Error: Please Specify Seed URL");
            System.out.println("Please Refer to ReadMe/Instructions");


        }
    }
}
