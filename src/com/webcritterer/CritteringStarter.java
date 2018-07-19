package com.webcritterer;

import java.io.IOException;

public class CritteringStarter {

    public static void main(String[] args) {
        Critterer critterer = new Critterer(); //initializes Critterer class
        critterer.load("http://www.uci.edu"); // INSERT SEED URL here
    }
}
