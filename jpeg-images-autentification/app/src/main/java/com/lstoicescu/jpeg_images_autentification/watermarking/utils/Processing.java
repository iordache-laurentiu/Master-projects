package com.lstoicescu.jpeg_images_autentification.watermarking.utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by lstoicescu on 12/29/2017.
 */

public class Processing {
    private static Processing singleton;

    private Processing() {

    }

    public static Processing getInstance() {
        if (singleton == null) {
            singleton = new Processing();
        }
        return singleton;
    }

    /**
     * Convert the provided input image into the YCrCb color space and change the format to 32F.
     * Those operations ar needed to be done in order to perform the DCT transform.
     *
     * @param img The given image on which the processing will take place
     * @return
     */
    public Mat preProcessing(Mat img) {
        Mat result = new Mat();

        Imgproc.cvtColor(img, result, Imgproc.COLOR_RGB2YCrCb);
        result.convertTo(result, CvType.CV_32F);

        return result;
    }


    /**
     * Convert the provided input image into the RGB color space anf change the format to 8U
     * Those operations are needed to be done in order to display the image in a useful manner.
     *
     * @param img The given image on which the processing will take place.
     * @return
     */
    public Mat postProcessing(Mat img) {
        Mat result = new Mat();

        img.convertTo(result, CvType.CV_8U);
        Imgproc.cvtColor(result, result, Imgproc.COLOR_YCrCb2RGB);

        return result;
    }
}
