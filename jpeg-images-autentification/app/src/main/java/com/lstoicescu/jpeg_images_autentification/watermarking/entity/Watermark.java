package com.lstoicescu.jpeg_images_autentification.watermarking.entity;

/**
 * Created by lstoicescu on 1/2/2018.
 */

public class Watermark {
    private final String TAG = "WATERMARK";

    private byte[] watermakr;
    private int watermarkBitPosition = 0;

    public Watermark(byte watermark[]) {
        this.watermakr = watermark;
    }

    public byte[] getWatermakr() {
        return watermakr;
    }

    public boolean getWatermarkBit() {
        if (watermarkBitPosition == (watermakr.length * 8) - 1) {
            watermarkBitPosition = 0;
        }

        watermarkBitPosition++;

        // return true for bit 1 and false for 0;
        return ((watermakr[(int) watermarkBitPosition / 8] >> (watermarkBitPosition % 8)) & 1) == 1;
    }

}
