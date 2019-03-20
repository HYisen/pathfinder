package net.alexhyisen.pathfinder.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Loader {
    private static float INF_VALUE = 1000;
    private List<double[]> rawVertexes;
    private List<double[]> vertexes;
    private List<int[]> shapes;

    private boolean changed = false;

    public static void main(String[] args) throws IOException {
        var one = new Loader();
        one.load();

        var stats1 = one.getVertexes()
                .stream()
                .flatMapToDouble(Arrays::stream)
                .summaryStatistics();
        System.out.println(one.getVertexes().size());
        System.out.println(stats1);
        var stats0 = one.getShapes()
                .stream()
                .flatMapToInt(Arrays::stream)
                .summaryStatistics();
        System.out.println(one.getShapes().size());
        System.out.println(stats0);
    }

    public void load(Path path) throws IOException {
        final List<String> lines = Files.readAllLines(path);
        final var info = lines.get(1).split(" ");
        final int ptrsSize = Integer.valueOf(info[0]);
        final int shpsSize = Integer.valueOf(info[1]);
        rawVertexes = IntStream
                .range(2, 2 + ptrsSize)
                .mapToObj(lines::get)
//                .peek(System.out::println)
                .map(v -> Arrays
                        .stream(v.split(" "))
                        .mapToDouble(str -> {
                            if ("-inf".equals(str)) {
                                return -INF_VALUE;
                            } else {
                                return Double.valueOf(str);
                            }
                        })
                        .toArray())
                .collect(Collectors.toList());
        shapes = IntStream
                .range(2 + ptrsSize, 2 + ptrsSize + shpsSize)
                .mapToObj(lines::get)
                .map(v -> Arrays
                        .stream(v.split(" "))
                        .skip(1)
                        .mapToInt(Integer::valueOf)
                        .toArray())
                .collect(Collectors.toList());
        setMatrix(new double[]{1, 0, 0, 0, 1, 0, 0, 0, 1});
    }

    public void setMatrix(double[] matrix) {
        vertexes = rawVertexes
                .stream()
                .map(v -> new double[]{
                        //I'v known that y(height) in input is inverted.
                        v[0] * matrix[0] - v[1] * matrix[1] + v[2] * matrix[2],
                        v[0] * matrix[3] - v[1] * matrix[4] + v[2] * matrix[5],
                        v[0] * matrix[6] - v[1] * matrix[7] + v[2] * matrix[8],
                })//I know that there is a better O(n^lg10) rather than O(lg^3) cross mul.
                .collect(Collectors.toList());
        changed = true;
        System.out.println("set matrix to " + Arrays.toString(matrix));
    }

    public void load() throws IOException {
        load(Paths.get(".", "manifold_final.off"));
    }

    public boolean hasChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public List<double[]> getVertexes() {
        return vertexes;
    }

    public List<int[]> getShapes() {
        return shapes;
    }

    public List<double[][]> getTriangles() {
        return shapes
                .stream()
                .map(v -> new double[][]{vertexes.get(v[0]), vertexes.get(v[1]), vertexes.get(v[2])})
                .collect(Collectors.toList());
    }
}
