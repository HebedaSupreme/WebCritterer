package com.webcritterer;

import java.io.IOException;
import java.io.InputStream;

public class CrittererBandwidthLimitation extends InputStream {
    private InputStream inputStream;
    long previousTimestamp = System.currentTimeMillis();
    long counter = 0;
    int second = 1000;
    long maxKilobytesPerSecond = 100;
    long maxBytesPerSecond = maxKilobytesPerSecond * 1000;

    public CrittererBandwidthLimitation(InputStream inStream) {
        inputStream = inStream;
    }

    @Override
    public int read() {
        int nextByte= -1;
        try {
                nextByte = inputStream.read();
                ++counter;
                long bytesPerSecond = counter;
                //System.out.println(bytesPerSecond);
                if(bytesPerSecond >= maxBytesPerSecond){
                    long currentTimestamp = System.currentTimeMillis();
                    if(previousTimestamp + second >= currentTimestamp){
                        Thread.sleep(previousTimestamp + second - currentTimestamp);
                    }
                    previousTimestamp = currentTimestamp;
                    counter = 0;
                }

        } catch (IOException io) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return nextByte;
    }
}







