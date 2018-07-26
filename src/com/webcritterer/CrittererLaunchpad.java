package com.webcritterer;

import java.util.List;

public class CrittererLaunchpad {

    //0...url or file file   1...pages 2...limiter

    public String[] args;
    public String errorMsg = "Error: Please Refer to ReadMe/Instructions";
    public String usageMsg = "Usage: run [SeedURL |OR| Directory Path to File with URL] <Number of Pages Maximum to Critter> [Number of Kilobytes Per Second Crawling Should Average --> 'No' if not needed]";
    public String otherSpecsMsg = "If other specifications are preferred, please refer to ReadMe/Instructions";
    Critterer critterer;
    long totalTimeRunnning;
    long startTimestamp;
    long endTimestamp;


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

        if (args.length < 2) {
            errorMessaging();
        } else {
            if (args[0].contains("www.") || args[0].contains(".org") || args[0].contains(".com")) {
                if (args[1].matches("[0-9]+")) {
                    if (args[2].matches("[0-9]+")) {
                        System.out.println("Running Configuration On Seed URL " + args[0] + " For " + args[1] + " Webpages At Bandwidth Limit Of " + args[2] + " Kilobytes/Second");
                        askSpecsMessaging();
                        launch();

                    } else {
                        args[2] = "false";
                        System.out.println("Running Configuration On Seed URL " + args[0] + " For " + args[1] + " Webpages With No Bandwidth Limit");
                        askSpecsMessaging();
                        launch();
                    }
                } else {
                    errorMessaging();
                }
            } else {
                if (args[1].matches("[0-9]+")) {
                    if (args[2].matches("[0-9]+")) {
                        System.out.println("Running Configuration on .txt File Containing URLs For "+ args[1] + " Webpages At Bandwidth Of " + args[2] + " Kilobytes/Second");
                        askSpecsMessaging();
                        launchForFile();

                    } else {
                        args[2] = "false";
                        System.out.println("Running Configuration on .txt File Containing URLs For "+ args[1] + " Webpages With No Bandwidth Limit");
                        askSpecsMessaging();
                        launchForFile();
                    }
                } else {
                    errorMessaging();
                }
            }
        }
    }

    public void launch() {
        String seedURL = args[0];
        startTimestamp = System.currentTimeMillis();
        critterer = new Critterer(args);
        critterer.addingurllist();
        critterer.load("https://" + seedURL);
        endTimestamp = System.currentTimeMillis();
        printMoreFinalStats();

    }

    public void launchForFile() {
        critterer = new Critterer(args);
        critterer.addingurllist();
        List<String> urlList = critterer.getPagesNeededToGoTo();
        String urlConvertedToString = String.valueOf(urlList.get(0));
        args[0] = urlConvertedToString;
        critterer.load("https://" + args[0]);
    }

    public void printMoreFinalStats() {
        totalTimeRunnning = (endTimestamp - startTimestamp) / 1000;
        System.out.println("Total Time Running By Timestamp: " + totalTimeRunnning + " seconds");
        long totalKilos = critterer.gettotalKilos();
        long avgByTimestamp = totalKilos / totalTimeRunnning;
        System.out.println("Average KB/sec by Timestamp: " + avgByTimestamp + " kilobytes/sec");
    }

}
