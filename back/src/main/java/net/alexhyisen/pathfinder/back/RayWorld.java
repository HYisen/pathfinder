package net.alexhyisen.pathfinder.back;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

public class RayWorld {
    private static TimeLogger tl = new TimeLogger("_world");
    private boolean[][][] data = new boolean[1000][1000][1000];

    public static void main(String[] args) {
        var world = new RayWorld();
        world
//                .drawLine(100, 100, 100, 900, 900, 100)
//                .drawLine(900, 900, 100, 100, 900, 900)
                .drawLine(100, 900, 900, 100, 100, 100)
                .drawPoint(300, 300, 300)
                .trinity();

    }

    public RayWorld drawPoint(int x, int y, int z) {
        data[x][y][z] = true;
        return this;
    }

    public RayWorld drawLine(int x0, int y0, int z0, int x1, int y1, int z1) {
        for (int i = 0; i < 1000; i++) {
            if (isBetween(i, x0, x1)) {
                for (int j = 0; j < 1000; j++) {
                    if (isBetween(j, y0, y1)) {
                        for (int k = 0; k < 1000; k++) {
                            if (isBetween(k, z0, z1)) {
                                double ratio = (double) (i - x0) / (x1 - x0);

                                System.out.println(String.format("scan (%3d,%3d,%3d) %5f", i, j, k, ratio));
                                if (ratio * (y1 - y0) == (j - y0) && ratio * (z1 - z0) == (k - z0)) {
                                    data[i][j][k] = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        tl.log(String.format("drawn (%3d,%3d,%3d) - (%3d,%3d,%3d)", x0, y0, z0, x1, y1, z1));
        return this;
    }

    private boolean isBetween(int orig, int alpha, int omega) {
        if (alpha > omega) {
            var temp = alpha;
            alpha = omega;
            omega = temp;
        }
        return orig >= alpha && orig <= omega;
    }

    public void trinity() {
        System.load("C:\\code\\opencv0\\build\\java\\x64\\opencv_java342.dll");
        var mat = Mat.eye(1000, 1000, CvType.CV_8UC1);
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {
                for (int k = 0; k < 1000; k++) {
                    if (data[i][j][k]) {
                        mat.put(500 - k / 2, i / 2, 100);
                        mat.put(500 - k / 2, j / 2 + 500, 100);
                        mat.put(1000 - j / 2, i / 2, 100);
                        break;
                    }
                }
            }
        }
        HighGui.namedWindow("Display", HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow("Display", mat);
        HighGui.waitKey(0);
    }
}
