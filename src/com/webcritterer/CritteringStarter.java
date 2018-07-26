package com.webcritterer;


public class CritteringStarter {
    //0...url 1...pages  2...limiter

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Error: Please Refer to ReadMe/Instructions");
        } else {
            if (args[0].contains("www.")) {
                String seedURL = args[0];
                if (args[1].matches("[0-9]+")) {
                    if (args[2].matches("[0-9]+")) {
                        System.out.println("Running Configuration On Seed URL " + args[0] + " For " + args[1] + " Webpages At Bandwidth Limit Of " + args[2] + " Kilobytes/Second");
                        System.out.println("If other specifications are preferred, please refer to ReadMe/Instructions");
                        long startTimestamp = System.currentTimeMillis();
                        Critterer critterer = new Critterer(args);
                        critterer.load("https://" + seedURL);
                        long endTimestamp = System.currentTimeMillis();
                        long totalTimeRunnning;
                        totalTimeRunnning = (endTimestamp - startTimestamp) / 1000;
                        System.out.println("Total Time Running By Timestamp: " + totalTimeRunnning + " seconds");
                        long totalKilos = critterer.gettotalKilos();
                        long avgByTimestamp = totalKilos / totalTimeRunnning;
                        System.out.println("Average KB/sec by Timestamp: " + avgByTimestamp + " kilobytes/sec");
                    } else {
                        args[2] = "false";
                        System.out.println("Running Configuration On Seed URL " + args[0] + " For " + args[1] + " Webpages With No Bandwidth Limit");
                        System.out.println("If other specifications are preferred, please refer to ReadMe/Instructions");
                        long startTimestamp = System.currentTimeMillis();
                        Critterer critterer = new Critterer(args);
                        critterer.load("https://" + seedURL);
                        long endTimestamp = System.currentTimeMillis();
                        long totalTimeRunnning;
                        totalTimeRunnning = (endTimestamp - startTimestamp) / 1000;
                        System.out.println("Total Time Running By Timestamp: " + totalTimeRunnning + " seconds");
                        long totalKilos = critterer.gettotalKilos();
                        long avgByTimestamp = totalKilos / totalTimeRunnning;
                        System.out.println("Average KB/sec by Timestamp: " + avgByTimestamp + " kilobytes/sec");
                    }
                } else {
                    System.out.println("Error: Please Refer to ReadMe/Instructions");
                }
            } else {
                System.out.println("Error: Please Refer to ReadMe/Instructions");
            }
        }
    }
}
