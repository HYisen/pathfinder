package net.alexhyisen.pathfinder.world;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

class ProgramBuilder {
    private int program;

    ProgramBuilder() {
        this.program = glCreateProgram();
    }

    private String loadShaderCode(String name) {
        try {
            byte[] bytes = getClass().getResourceAsStream("/shader/" + name + ".glsl").readAllBytes();
            return new String(bytes);
        } catch (IOException e) {
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

    @SuppressWarnings("SameParameterValue")
    ProgramBuilder addFragmentShader(String name) {
        return addShader(name, GL_FRAGMENT_SHADER);
    }

    @SuppressWarnings("SameParameterValue")
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
