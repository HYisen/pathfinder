package net.alexhyisen.pathfinder.world;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

class ProgramBuilder {
    private int program;

    ProgramBuilder() {
        this.program = glCreateProgram();
    }

    private static String loadShaderCode(String name) {
        var loader = Thread.currentThread().getContextClassLoader();
        try {
            var url = Objects.requireNonNull(loader.getResource("shader/" + name + ".glsl"));
            return Files
                    .lines(Path.of(url.toURI()))
                    .collect(Collectors.joining("\n"));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    int build() {
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
            throw new RuntimeException(glGetProgramInfoLog(program));
        }
        return program;
    }

    ProgramBuilder addVertexShader(String name) {
        return addShader(name, GL_VERTEX_SHADER);
    }

    ProgramBuilder addFragmentShader(String name) {
        return addShader(name, GL_FRAGMENT_SHADER);
    }

    ProgramBuilder addGeometryShader(String name) {
        return addShader(name, GL_GEOMETRY_SHADER);
    }

    @SuppressWarnings("WeakerAccess")
    ProgramBuilder addShader(String name, int type) {
        int shader = glCreateShader(type);
        glShaderSource(shader, loadShaderCode(name));
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException(glGetShaderInfoLog(shader));
        }
        glAttachShader(program, shader);
        return this;
    }
}
