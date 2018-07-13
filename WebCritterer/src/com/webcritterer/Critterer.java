package com.webcritterer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Critterer {
    private static final long MaximumPagesToGoTo = 1000000000;
    private Set<String> pagesAlreadyHit = new HashSet<String>();
    private List<String> pagesNeededToGoTo = new LinkedList<String>();


    private String nextUrl() {
        String nextUrl;
        do {
            nextUrl = this.pagesNeededToGoTo.remove(0);
        } while (this.pagesAlreadyHit.contains(nextUrl));
        this.pagesAlreadyHit.add(nextUrl);
        return nextUrl;
    }


    public void load(String url) {
        while (this.pagesAlreadyHit.size() < MaximumPagesToGoTo) {
            String currentUrl;
            CrittererEat scramble = new CrittererEat();
            if (this.pagesNeededToGoTo.isEmpty()) {
                currentUrl = url;
                this.pagesAlreadyHit.add(url);
            } else {
                currentUrl = this.nextUrl();
            }
            scramble.critter(currentUrl);
            this.pagesNeededToGoTo.addAll(scramble.getLinks());
        }
        System.out.println("\n**Done** Visited " + this.pagesAlreadyHit.size() + " web page(s)");
    }

}