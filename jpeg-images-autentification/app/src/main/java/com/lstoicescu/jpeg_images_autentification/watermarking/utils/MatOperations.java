package com.lstoicescu.jpeg_images_autentification.watermarking.utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 * Created by lstoicescu on 12/29/2017.
 */

public class MatOperations {

    private static MatOperations singleton;

    private MatOperations() {

    }

    public static MatOperations getInstance() {
        if (singleton == null) {
            singleton = new MatOperations();
        }
        return singleton;
    }

    /**
     * Inserts into img a block at the given position.
     *
     * @param img      The image in which the block will be inserted;
     * @param block    The block which will be inserted;
     * @param position Position where the block will be inserted (consider the top left corner).
     * @return
     */
    public Mat insertBlockInMat(Mat img, Mat block, Point position) {
        Mat result = img;

        for (int i = 0; i < block.height(); i++) {
            for (int j = 0; j < block.width(); j++) {
                result.put((int) position.y + j, (int) position.x + i, block.get(i, j)[0]);
            }
        }

        return result;
    }


    /**
     * Converts a given matrix in a vector using the zig-zag pattern used in JPEG compression.
     *
     * @param kernel The NxN block from the image.
     * @return
     */
    public double[] zigzag(Mat kernel) {
        int dim = kernel.rows();
        double[] result = new double[dim * dim];
        int index = 0;

        for (int i = 0; i < 2 * dim; i++) {
            if (i % 2 == 0) {
                for (int j = i; j >= 0; j--) {
                    if ((j < dim) && (i - j < dim)) {
                        result[index] = kernel.get(j, i - j)[0];
                        index++;
                    }
                }
            } else {
                for (int j = 0; j <= i; j++) {
                    if ((j < dim) && (i - j < dim)) {
                        result[index] = kernel.get(j, i - j)[0];
                        index++;
                    }
                }
            }
        }

        return result;
    }


    /**
     * Converts a given array in a vector using the inverse zig-zag pattern used in JPEG compression.
     *
     * @param array The given array.
     * @return
     */
    public Mat izigzag(double[] array) {
        int dim = (int) Math.sqrt(array.length);
        Mat result = new Mat(dim, dim, CvType.CV_32F);
        int index = 0;

        for (int i = 0; i < 2 * dim; i++) {
            if (i % 2 == 0) {
                for (int j = i; j >= 0; j--) {
                    if ((j < dim) && (i - j < dim)) {
                        result.put(j, i - j, array[index]);
                        index++;
                    }
                }
            } else {
                for (int j = 0; j <= i; j++) {
                    if ((j < dim) && (i - j < dim)) {
                        result.put(j, i - j, array[index]);
                        index++;
                    }
                }
            }
        }

        return result;
    }
}
