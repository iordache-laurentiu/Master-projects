package com.lstoicescu.jpeg_images_autentification.watermarking.controller;


import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.lstoicescu.jpeg_images_autentification.watermarking.ValidationActivity;
import com.lstoicescu.jpeg_images_autentification.watermarking.entity.Watermark;
import com.lstoicescu.jpeg_images_autentification.watermarking.utils.MatOperations;
import com.lstoicescu.jpeg_images_autentification.watermarking.utils.Processing;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lstoicescu on 12/11/2017.
 */

public class WatermarkController {
    private final String TAG = "WATERMARK_CONTROLLER";

    private int x = -16, y = 0;
    private static int height, width;

    private final int qf1 = 50; // Quantization factor used to embed the message
    private final int weight = 1;
    private Mat quantizationGrid;
    private double[] d;


    public WatermarkController() {
        quantizationGrid = new Mat(8, 8, CvType.CV_8U);
        quantizationGrid.put(0, 0,
                16, 11, 10, 16, 24, 40, 51, 61,
                12, 12, 14, 19, 26, 58, 60, 55,
                14, 13, 16, 24, 40, 57, 69, 56,
                14, 17, 22, 29, 51, 87, 80, 62,
                18, 22, 37, 56, 68, 109, 103, 77,
                24, 35, 55, 64, 81, 104, 113, 92,
                49, 64, 78, 87, 103, 121, 120, 101,
                72, 92, 95, 98, 112, 100, 103, 99);
        d = MatOperations.getInstance().zigzag(quantizationGrid);

        int qScale = 0;
        if (qf1 <= 50) {
            qScale = Math.round(50 / qf1);
        } else {
            qScale = (200 - 2 * qf1) / 100;
        }

        for (Double db : d) {
            db = db * weight * qScale;
        }


    }

    private static void setImageSize(double imgHeight, double imgWidth) {
        height = (int) imgHeight;
        width = (int) imgWidth;
    }

    /**
     * Returns the last selected block. This method was declared in order to use it in multi thread
     * processing.
     *
     * @return
     */
    @NonNull
    private synchronized Point getBlockPosition(boolean next) {
        if (next) {
            y = (x < width - 31) ? y : (y + 8);
            x = (x < width - 31) ? (x + 16) : 0;
        }
        return new Point(x, y);
    }

    /**
     * Inserts the given watermark into the image
     *
     * @param img       - The image received from the camera or from de sd card
     * @param watermark - The watermark generated based on the selected private key
     * @return
     */
    public Mat insertWatermarkIntoImage(Mat img, Watermark watermark, boolean insert) {
        Mat result;
        Mat luminance;
        List<Mat> yCrCb = new ArrayList<>(3);
        this.x = -16;
        this.y = 0;

        System.out.println("DIMENSIUNE: " + img.width() + " " + img.height());

        WatermarkController.setImageSize(img.height(), img.width());

        // Convert the given img into YCbCr color space.
        result = Processing.getInstance().preProcessing(img);
        Core.split(result, yCrCb);
        luminance = yCrCb.get(0);

        // Compute the pairs number of 8x8 blocks
        int pairsCount = (luminance.width() / 16) * (luminance.height() / 8);

        // For each iteration take the next slice of 8x16 from luminance and perform the insertion process
        for (int i = 0; i < pairsCount; i++) {
            Point startPosition = getBlockPosition(true);
            Point endPosition = new Point(startPosition.x + 16, startPosition.y + 8);
            Mat slice = luminance.submat(new Rect(startPosition, endPosition));


            boolean wm = watermark.getWatermarkBit();
            if (insert) {
                watermarkIntoBlock(slice, wm, true);
            } else {
                watermarkIntoBlock(slice, wm, false);
            }

        }

        // Convert the result back to the RGB color space.
        yCrCb.set(0, luminance);
        Core.merge(yCrCb, result);
        result = Processing.getInstance().postProcessing(result);

        return result;
    }

    /**
     * Insert message bits in pairs of blocks from the image
     *
     * @param slice
     * @param wm
     * @param insert
     * @return
     */
    public Mat watermarkIntoBlock(Mat slice, boolean wm, boolean insert) {
        Mat result = new Mat(slice.rows(), slice.cols(), slice.type());
        Mat leftBlockDCT = new Mat();
        Mat rightBlockDCT = new Mat();
        double[] leftArr, rightArr;

        // Break the 8x16 slice in two blocks of 8x8
        Mat leftBlock = slice.submat(0, 8, 0, 8);
        Mat rightBlock = slice.submat(0, 8, 8, 16);

        // Apply DCT transform on both blocks
        // Transform the matrix into array with the zigzag pattern
        Core.dct(leftBlock, leftBlockDCT);
        Core.dct(rightBlock, rightBlockDCT);
        leftArr = MatOperations.getInstance().zigzag(leftBlockDCT);
        rightArr = MatOperations.getInstance().zigzag(rightBlockDCT);

        double absDiff;
        double left, right;
        int wat = (wm) ? 1 : 0;

        if (insert) {
            // Insert the watermark in each pair that match the condition (p - q <= 2 * D)
            // If the difference matches the other condition (p - q > 2 * D) adjust the difference to be 3D

            for (int i = 2; i < 64; i++) {

                left = leftArr[i];
                right = rightArr[i];
                absDiff = Math.abs(left - right);

                // Identify the pairs in which the message can be embedded
                if (absDiff <= 2 * d[i]) {

                    // Embed only one bit into a pair of blocks adding D/2 to one element of a block
                    // and subtracting D/2 from the other one;
                    leftArr[i] = (left < right) ? (left + absDiff / 2 + (wat - 0.5) * d[i]) : (left - absDiff / 2 + (wat - 0.5) * d[i]);
                    rightArr[i] = (left < right) ? (right - absDiff / 2 + (0.5 - wat) * d[i]) : (right + absDiff / 2 + (0.5 - wat) * d[i]);
                    break;
                } else {
                    if (absDiff <= 3 * d[i]) {
                        double diffUtil3D = 3 * d[i] - absDiff;

                        // Rise the absolute difference to 3D for all blocks that have the difference
                        // less than 3D but greater then 2D
                        leftArr[i] = (left < right) ? (left - diffUtil3D / 2) : (left + diffUtil3D / 2);
                        rightArr[i] = (left < right) ? (right + diffUtil3D / 2) : (right - diffUtil3D / 2);
                    }
                }
            }
        } else {
            // Extract the watermark from image
            for (int i = 2; i < 64; i++) {
                left = leftArr[i];
                right = rightArr[i];
                absDiff = Math.abs(left - right);


                // Identify the pairs from which the message can be embedded
                if (absDiff < 2 * d[i]) {
                    if (absDiff > 0.5 * d[i] & absDiff < 1.5 * d[i]) {
                        int extractedBit = (left - right < 0) ? 0 : 1;

                        System.out.println("ABSDIFF: " + absDiff + " D[i]: " + d[i] * 0.5 + " " + d[i] + " " + d[i] * 1.5 + "     EXTRACTED: " + extractedBit + " WAT: " + wat);

                        if (extractedBit != wat) {
                            leftArr = new double[64];
                            rightArr = new double[64];
                        }
                    } else {
                        leftArr = new double[64];
                        rightArr = new double[64];
                    }

                    break;
                }
            }
        }

        // Transform the array back into matrix with the reverse zigzag pattern
        // Apply inverse DCT transform on both blocks
        leftBlockDCT = MatOperations.getInstance().izigzag(leftArr);
        rightBlockDCT = MatOperations.getInstance().izigzag(rightArr);
        Core.idct(leftBlockDCT, leftBlock);
        Core.idct(rightBlockDCT, rightBlock);

        return result;
    }


}