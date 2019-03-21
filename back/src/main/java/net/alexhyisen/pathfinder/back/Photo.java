package net.alexhyisen.pathfinder.back;

import org.opencv.core.*;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.nio.file.Paths;

public class Photo {
    private static double CAMERA_FACTOR = 1000;
    private static double CAMERA_CX = 325.5;
    private static double CAMERA_CY = 253.5;
    private static double CAMERA_FX = 518.0;
    private static double CAMERA_FY = 519.0;

    public static void main(String[] args) {
//        System.load("C:\\code\\opencv\\build\\java\\x64\\opencv_java400.dll");
        System.load("C:\\code\\opencv0\\build\\java\\x64\\opencv_java342.dll");
        var mat = Mat.eye(100, 100, CvType.CV_8UC1);
//        System.out.println(mat.dump());

        mat = Imgcodecs.imread("C:\\Users\\alexh\\Pictures\\Inspiration\\Conflict_on_G7.jpg");

        HighGui.namedWindow("Display", HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow("Display", mat);
        HighGui.waitKey(0);
        mat = Mat.eye(100, 100, CvType.CV_8UC1);
        HighGui.imshow("Display", mat);
        HighGui.waitKey(0);

        var matR = Mat.eye(640, 480, CvType.CV_8UC1);
        var matD = Mat.eye(640, 480, CvType.CV_8UC1);
        HighGui.namedWindow("DisplayR", HighGui.WINDOW_AUTOSIZE);
        HighGui.namedWindow("DisplayD", HighGui.WINDOW_AUTOSIZE);

        var detector = FeatureDetector.create(FeatureDetector.FAST);
//        var descriptor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
//        var detector = FastFeatureDetector.create();
//        var matcher = FlannBasedMatcher.create(FlannBasedMatcher.FLANNBASED);
//        var matches = MatOfDMatch.

        var path = Paths.get("C:\\code\\dataset\\basement_0001a");
        var m = new Matcher(path);
        Mat old = null;
        MatOfKeyPoint one = new MatOfKeyPoint();
        MatOfKeyPoint two = new MatOfKeyPoint();
        Mat show = new Mat();
        while (m.hasNext()) {
            var nd = m.next();
            matR = Imgcodecs.imread(path.resolve(nd.getR()).toAbsolutePath().toString(), Imgcodecs.IMREAD_COLOR);
            HighGui.imshow("DisplayR", matR);
            matD = Imgcodecs.imread(path.resolve(nd.getD()).toAbsolutePath().toString(), Imgcodecs.IMREAD_GRAYSCALE);
            HighGui.imshow("DisplayD", matD);

            if (old != null) {
                detector.detect(old, one);
                detector.detect(matR, two);
                System.out.println(one.size() + " & " + two.size());
                for (KeyPoint keyPoint : two.toList()) {
                    double x = (keyPoint.pt.x - CAMERA_CX) / CAMERA_FX;
                    double y = (keyPoint.pt.y - CAMERA_CY) / CAMERA_FY;
                    final double[] doubles = matD.get((int) keyPoint.pt.x, (int) keyPoint.pt.y);
                    if (doubles == null) {
                        continue;
                    }
                    double z = doubles[0] / CAMERA_FACTOR;
                    System.out.println(String.format("(%f,%f,%f)", x, y, z));
                }

                Features2d.drawKeypoints(matR, one, show, Scalar.all(-1), Features2d.DRAW_RICH_KEYPOINTS);
                HighGui.imshow("DisplayR", show);
            }

            HighGui.waitKey(0);
            System.out.println(nd);

            old = matR;
        }
        System.out.println("end");
    }
}
