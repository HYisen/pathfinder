package net.alexhyisen.pathfinder.gui;

import net.alexhyisen.pathfinder.utility.Matcher;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.nio.file.Paths;

public class Photo {
    public static void main(String[] args) {
        System.load("C:\\code\\opencv\\build\\java\\x64\\opencv_java400.dll");
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

        var path = Paths.get("C:\\code\\dataset\\basement_0001a");
        var m = new Matcher(path);
        while (m.hasNext()) {
            var nd = m.next();
            matR = Imgcodecs.imread(path.resolve(nd.getR()).toAbsolutePath().toString());
            HighGui.imshow("DisplayR", matR);
            matD = Imgcodecs.imread(path.resolve(nd.getD()).toAbsolutePath().toString());
            HighGui.imshow("DisplayD", matD);
            HighGui.waitKey(0);
            System.out.println(nd);
        }
        System.out.println("end");
    }
}
