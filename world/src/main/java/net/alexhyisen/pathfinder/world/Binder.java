package net.alexhyisen.pathfinder.world;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Binder {
    private int vao;
    private int vbo;
    private int ebo;

    private int program;

    private float[] vertices;
    private int[] indices;

    public Binder(float[] vertices, int[] indices, int usage) {
        setVertices(vertices).setIndices(indices).init(usage);
    }

    public Binder() {
    }

    public static void main(String[] args) {
//        var one = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
//        System.out.println(one);
//        var trans = new Matrix4f();
//        System.out.println(trans);
//        trans.translateLocal(1.0f, 1.0f, 1.0f);
//        trans.rotate((float) toRadians(90), 0, 0, 1).scale(0.5f);
//        System.out.println(trans);
//
//        trans = new Matrix4f();
//        System.out.println(trans);
//        trans.rotate((float) toRadians(90), 0, 0, 1).scale(0.5f);
//        trans.translate(1.0f, 1.0f, 1.0f);
//        System.out.println(trans);

//        var fb = BufferUtils.createFloatBuffer(16);
//        var trans = new Matrix4f();
//        System.out.println(new Matrix4f(trans.get(fb)));

        var pos = new Vector3f(0, 0, 3);
        System.out.println(pos);
        System.out.println(pos.add(1, 0, 0));
        System.out.println(pos);
    }

    public Binder setVertices(float... vertices) {
        this.vertices = vertices;
        return this;
    }

    public Binder setIndices(int... indices) {
        this.indices = indices;
        return this;
    }

    public int getProgram() {
        return program;
    }

    public Binder setProgram(int program) {
        this.program = program;
        return this;
    }

    public Binder init(int usage) {
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
