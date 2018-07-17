package com.webcritterer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Critterer {
    public static final long MaximumPagesToGoTo = 1000000000; //ASSIGN THE MAXIMUM NUMBER OF PAGES TO TRAVEL TO HERE
    private Set<String> pagesAlreadyHit = new HashSet<String>();
    private List<String> pagesNeededToGoTo = new LinkedList<String>();
    //created a list of pages to go to and a set of pages already hit (set because of unique entries)

    //get first entry from pagesNeededToGoTo, make sure URL hasn't already been visited, or find the next one to visit
    private String nextUrl() {
        String nextUrl;
        do {
            nextUrl = this.pagesNeededToGoTo.remove(0);
        } while (this.pagesAlreadyHit.contains(nextUrl));
        this.pagesAlreadyHit.add(nextUrl);
        return nextUrl;
    }

    public void load(String url) {
        while (this.pagesAlreadyHit.size() < MaximumPagesToGoTo) { //while pagesalreadyhit is under limit
            String currentUrl;
            CrittererEat scramble = new CrittererEat(); //initializes CrittererEat which will travel through a connection and parse the content
            if (this.pagesNeededToGoTo.isEmpty()) {
                currentUrl = url;   //if pages to go to is empty, the currentURL is the url to travel to, and add said url to list of those already hit
                this.pagesAlreadyHit.add(url);
            } else {
                currentUrl = this.nextUrl(); //if list not empty, currentURL will be next
            }

            scramble.critter(currentUrl); //method where CrittererEat is called to connect to and parse URL
            this.pagesNeededToGoTo.addAll(scramble.getLinks()); //collect more URLs
        }
        System.out.println("\n**Done** Visited " + this.pagesAlreadyHit.size() + " web page(s)"); //print message on text file
    }

}
