package com.webcritterer;

import java.io.IOException;
import java.io.InputStream;

public class CrittererBandwidthLimitation extends InputStream { //extends inputstream from CrittererEat class (for wrapping purposes)
    long maxKilobytesPerSecond = 100; //ASSIGN THE MAXIMUM NUMBER OF KILOBYTES PER SECOND HERE (BANDWIDTH CONSUMPTION)
    private InputStream inputStream;
    long previousTimestamp = System.currentTimeMillis(); //creates a timestamp for later use
    long counter = 0; //counter used to count bytes out of stream
    int second = 1000; //1 second
    long sleepincrement1 = maxKilobytesPerSecond - (7 * (maxKilobytesPerSecond / 10)); //First increment in stream where sleep is called
    long bytesAtInc1 = sleepincrement1 * 1000; //Kilobytes to bytes conversion


    public CrittererBandwidthLimitation(InputStream inStream) {
        inputStream = inStream;
    } //inherits inputstream from CrittererEat class

    @Override //override so that inputstream is only read once
    public int read() {
        int nextByte = -1;
        try {
            nextByte = inputStream.read(); //read by single byte
            ++counter; //increment counter per byte
            long bytes = counter;
            //System.out.println(bytes);
            if (bytes >= bytesAtInc1) { //if bytes counted exceeds the first increment
                long currentTimestamp = System.currentTimeMillis(); //create timestamp
                if (previousTimestamp + second >= currentTimestamp) { //verify the bytes were read within a second-long interval
                    Thread.sleep(second * 3); //if bytes per second exceeded limit sleep for three seconds
                }
                previousTimestamp = currentTimestamp; //reset timestamp
                counter = 0; //reset bytes counter
            }
        } catch (IOException io) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return nextByte;
    }
}







