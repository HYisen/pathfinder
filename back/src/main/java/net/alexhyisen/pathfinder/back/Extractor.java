package net.alexhyisen.pathfinder.back;

import org.opencv.imgcodecs.Imgcodecs;

import java.util.Arrays;

public class Extractor {
    public static void main(String[] args) {
        System.load("C:\\code\\opencv\\build\\java\\x64\\opencv_java400.dll");
//        var mat = Mat.eye(100, 100, CvType.CV_8UC1);
////        System.out.println(mat.dump());
//
//        mat = Imgcodecs.imread("C:\\Users\\alexh\\Pictures\\Inspiration\\Conflict_on_G7.jpg");
//
//        HighGui.namedWindow("Display", HighGui.WINDOW_AUTOSIZE);
//        HighGui.imshow("Display", mat);
//        HighGui.waitKey(0);
//        mat = Mat.eye(100, 100, CvType.CV_8UC1);
//        HighGui.imshow("Display", mat);
//        HighGui.waitKey(0);
//
//        var matR = Mat.eye(640, 480, CvType.CV_8UC1);
//        var matD = Mat.eye(640, 480, CvType.CV_8UC1);
//        HighGui.namedWindow("DisplayR", HighGui.WINDOW_AUTOSIZE);
//        HighGui.namedWindow("DisplayD", HighGui.WINDOW_AUTOSIZE);
//
//        var path = Paths.get("C:\\code\\dataset\\basement_0001a");
//        var m = new Matcher(path);
//        while (m.hasNext()) {
//            var nd = m.next();
//            matR = Imgcodecs.imread(path.resolve(nd.getR()).toAbsolutePath().toString());
//            HighGui.imshow("DisplayR", matR);
//            matD = Imgcodecs.imread(path.resolve(nd.getD()).toAbsolutePath().toString());
//            HighGui.imshow("DisplayD", matD);
//            HighGui.waitKey(0);
//            System.out.println(nd);
//        }
//        System.out.println("end");

        var rgb = Imgcodecs.imread("C:\\code\\dataset\\basement_0001a\\r-1316653580.678388-1328502346.ppm", Imgcodecs.IMREAD_COLOR);
        var depth = Imgcodecs.imread("C:\\code\\dataset\\basement_0001a\\d-1316653580.683254-1328151343.pgm", Imgcodecs.IMREAD_ANYDEPTH);

        for (int i = 0; i < depth.cols(); i++) {
            for (int j = 0; j < depth.rows(); j++) {
                final double[] ptr = depth.get(i, j);
                if (ptr != null) {
                    System.out.println("at (" + i + "," + j + ")");
                    System.out.println(Arrays.toString(ptr));
                    System.out.println(Arrays.toString(rgb.get(i, j)));
                }
            }
        }
    }
}
