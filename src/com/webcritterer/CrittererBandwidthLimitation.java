package com.webcritterer;

import java.io.IOException;
import java.io.InputStream;

public class CrittererBandwidthLimitation extends InputStream {
    long maxKilobytesPerSecond = 100;
    private InputStream inputStream;
    long previousTimestamp = System.currentTimeMillis();
    long counter = 0;
    int second = 1000;
    long maxbytespersec = maxKilobytesPerSecond * 1000;


    public CrittererBandwidthLimitation(InputStream inStream) {
        inputStream = inStream;
    }

    @Override
    public int read() {
        int nextByte = -1;
        try {
            nextByte = inputStream.read(); //read by single byte
            ++counter; //increment counter per byte
            long bytes = counter;
            //System.out.println(bytes);
            if (bytes >= maxbytespersec) { //if bytes counted exceeds the first increment
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







