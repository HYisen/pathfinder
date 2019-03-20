package net.alexhyisen.pathfinder.world;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

class TriBinder {
    private int vao;
    private int vbo;
    private float[] data;
    private int program;

    private int count;

    TriBinder setData(float[] data) {
        this.data = data;
        this.count = data.length / 12;
        return this;
    }

    int getVao() {
        return vao;
    }

    int getProgram() {
        return program;
    }

    TriBinder setProgram(int program) {
        this.program = program;
        return this;
    }

    int getCount() {
        return count;
    }

    @SuppressWarnings("SameParameterValue")
    TriBinder init(int usage) {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, usage);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 48, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 48, 12);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 48, 24);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, 48, 36);
        glBindVertexArray(0);
        return this;
    }

    void delete() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }
}
