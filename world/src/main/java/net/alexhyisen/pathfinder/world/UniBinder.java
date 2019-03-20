package net.alexhyisen.pathfinder.world;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

class UniBinder {
    private int vao;
    private int vbo;
    private int ebo;

    private int program;

    private float[] vertices;
    private int[] indices;

    UniBinder setVertices(float... vertices) {
        this.vertices = vertices;
        return this;
    }

    UniBinder setIndices(int... indices) {
        this.indices = indices;
        return this;
    }

    int getProgram() {
        return program;
    }

    UniBinder setProgram(int program) {
        this.program = program;
        return this;
    }

    @SuppressWarnings("SameParameterValue")
    UniBinder init(int usage) {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, usage);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, usage);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 24, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 24, 12);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
        return this;
    }

    int getVao() {
        return vao;
    }

    int getCount() {
        return indices.length;
    }

    void delete() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }
}
