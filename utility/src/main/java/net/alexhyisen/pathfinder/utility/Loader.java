package net.alexhyisen.pathfinder.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Loader {
    private static float INF_VALUE = 1000;
    private List<double[]> vertexes;
    private List<int[]> shapes;

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
        final Map<Integer, List<String[]>> collect = Files
                .lines(path)
                .sequential()//parallel will cause disorder in id
                .skip(2)
                .map(v -> v.split(" "))
                .collect(Collectors.groupingBy(v -> v.length));

        final List<String> lines = Files.readAllLines(path);
        final var info = lines.get(1).split(" ");
        final int ptrsSize = Integer.valueOf(info[0]);
        final int shpsSize = Integer.valueOf(info[1]);
        vertexes = IntStream
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
    }

    public void load() throws IOException {
        load(Paths.get(".", "manifold_final.off"));
    }

    public List<double[]> getVertexes() {
        return vertexes;
    }

    public List<int[]> getShapes() {
        return shapes;
    }
}
