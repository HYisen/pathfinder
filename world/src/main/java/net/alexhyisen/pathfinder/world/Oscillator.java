package net.alexhyisen.pathfinder.world;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Oscillator {
    private final float rank;
    private int count = 1;
    private boolean orientation = true;

    public Oscillator(int rank) {
        this.rank = rank;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        var loader = Thread.currentThread().getContextClassLoader();
        final URL resource = loader.getResource("shader/one.glsl");
        Files.lines(Path.of(resource.toURI())).forEach(System.out::println);
    }

    float next() {
        if (count == rank || count == 0) {
            orientation = !orientation;
        }
        if (orientation) {
            count++;
        } else {
            count--;
        }
        return count / rank;
    }
}
