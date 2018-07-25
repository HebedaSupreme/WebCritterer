package com.webcritterer;


public class CritteringStarter {
    //0...url 1...urlfile  2...pages  3...limiter

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Error: Please Refer to ReadMe/Instructions");
        } else {
            if (args[0].contains("www.")) {
                String seedURL = args[0];
                if (args[2].matches("[0-9]+")) {
                    if (args[3].matches("[0-9]+")) {
                        System.out.println("Running Configuration On Seed URL " + args[0] + " For " + args[2] + " Webpages At Bandwidth Limit Of " + args[3] + "Kilobytes/Second");
                        System.out.println("If other specifications are preferred, please refer to ReadMe/Instructions");
                        long startTimestamp = System.currentTimeMillis();
                        Critterer critterer = new Critterer(args);
                        critterer.load("https://" + seedURL);
                        critterer.addingurllist();
                        long endTimestamp = System.currentTimeMillis();
                        long totalTimeRunnning;
                        totalTimeRunnning = (endTimestamp - startTimestamp) / 1000;
                        System.out.println("Total Time Running By Timestamp: " + totalTimeRunnning + " seconds");
                        long totalKilos = critterer.gettotalKilos();
                        long avgByTimestamp = totalKilos / totalTimeRunnning;
                        System.out.println("Average KB/sec by Timestamp: " + avgByTimestamp + " kilobytes/sec");
                    } else {
                        args[3] = "false";
                        System.out.println("Running Configuration On Seed URL " + args[0] + " For " + args[2] + " Webpages With No Bandwidth Limit");
                        System.out.println("If other specifications are preferred, please refer to ReadMe/Instructions");
                        long startTimestamp = System.currentTimeMillis();
                        Critterer critterer = new Critterer(args);
                        critterer.load("https://" + seedURL);
                        critterer.addingurllist();
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
