package net.alexhyisen.pathfinder.world;

import net.alexhyisen.pathfinder.utility.Loader;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.stream.Stream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class World {
    private Map<String, UniBinder> binders = new HashMap<>();

    private Loader loader;

    private boolean[] keys = new boolean[1024];
    private Camera camera = new Camera(keys);

    private long window = -1;

    private Mode itemMode = Mode.UNI;
    private boolean changeMode = true;
    private UniBinder uniBinder;
    private TriBinder triBinder;

    private float metric = 50.0f;
    private Random rand = new Random(17);

    private FloatBuffer[] fb = Stream
            .generate(() -> BufferUtils.createFloatBuffer(16))
            .limit(4)
            .toArray(FloatBuffer[]::new);

    public World(Loader loader) {
        this.loader = loader;
    }

    private static void drawTemplate(int program, int vao,
                                     Matrix4f model, Matrix4f transform,
                                     Camera camera, FloatBuffer[] fb) {
        glUseProgram(program);
        glBindVertexArray(vao);
        glUniformMatrix4fv(glGetUniformLocation(program, "transform"), false,
                transform.get(fb[0]));
        glUniformMatrix4fv(glGetUniformLocation(program, "model"), false,
                model.get(fb[1]));
        glUniformMatrix4fv(glGetUniformLocation(program, "view"), false,
                camera.getView().get(fb[2]));
        glUniformMatrix4fv(glGetUniformLocation(program, "projection"), false,
                camera.getProjection().get(fb[3]));
    }

    public static void main(String[] args) {
        var loader = new Loader();
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        var world = new World(loader);
        world.open(true);
    }

    public float[] getCameraInfo() {
        return camera.getData();
    }

    public void setCameraInfo(float[] data) {
        camera.setData(data);
    }

    public void setItemMode(Mode itemMode) {
        this.itemMode = itemMode;
        changeMode = true;
    }

    private void draw(TriBinder binder, Matrix4f model, Matrix4f transform) {
        drawTemplate(binder.getProgram(), binder.getVao(), model, transform, camera, fb);

        glDrawArrays(GL_POINTS, 0, binder.getCount());

        glBindVertexArray(0);
    }

    private void draw(UniBinder binder, Matrix4f model, Matrix4f transform) {
        drawTemplate(binder.getProgram(), binder.getVao(), model, transform, camera, fb);

        glDrawElements(GL_TRIANGLES, binder.getCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }

    private void draw(String name, Matrix4f model, Matrix4f transform) {
        draw(binders.get(name), model, transform);
    }

    public void close() {
        glfwSetWindowShouldClose(window, true);
    }

    public void open(boolean bindMouse) {
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(1000, 1000, "World", 0, 0);
        if (window == 0) {
            System.out.println("Failed to creat GLFW window.");
            glfwTerminate();
            return;
        }


        glfwMakeContextCurrent(window);

        glfwSetWindowPos(window, 100, 100);

        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                keys[key] = true;
                switch (key) {
                    case GLFW_KEY_ESCAPE:
                        glfwSetWindowShouldClose(window, true);
                        break;
                    case GLFW_KEY_PAGE_UP:
                        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                        break;
                    case GLFW_KEY_PAGE_DOWN:
                        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                        break;
                    case GLFW_KEY_HOME:
                        glEnable(GL_DEPTH_TEST);
                        break;
                    case GLFW_KEY_END:
                        glDisable(GL_DEPTH_TEST);
                        break;
                    case GLFW_KEY_F:
                        camera.print();
                        break;
                    default:
                        break;
                }
            } else if (action == GLFW_RELEASE) {
                keys[key] = false;
            }
        });

        if (bindMouse) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            glfwSetCursorPosCallback(window, camera::handleCursor);

            glfwSetScrollCallback(window, camera::handleScroll);
        }

        GL.createCapabilities();

        var program = new ProgramBuilder().addVertexShader("one").addFragmentShader("two").build();
        var geoProgram = new ProgramBuilder()
                .addVertexShader("tri_v")
                .addGeometryShader("tri_g")
                .addFragmentShader("two").build();

        binders.put("one", new UniBinder()
                .setVertices(
                        -0.8f, 0, 0.0f, 1.0f, 0.0f, 0.0f,
                        0, 0.8f, 0.0f, 0.0f, 1.0f, 0.0f,
                        0.8f, 0, 0.0f, 0.0f, 0.0f, 1.0f,
                        0, -0.8f, 0.0f, 0.0f, 0.0f, 0.0f,
                        0, 0, 0.2f, 1f, 1f, 1f
                )
                .setIndices(
                        0, 1, 4,
                        1, 2, 4,
                        2, 3, 4,
                        0, 3, 4
                )
                .init(GL_STATIC_DRAW)
                .setProgram(program)
        );

        binders.put("land", new UniBinder()
                .setVertices(
                        1, 1, 0.0f, 0.212f, 0.180f, 0.169f,
                        1, -1, 0.0f, 0.212f, 0.180f, 0.169f,
                        -1, -1, 0.0f, 0.212f, 0.180f, 0.169f,
                        -1, 1, 0.0f, 0.212f, 0.180f, 0.169f
                )
                .setIndices(
                        0, 1, 2,
                        3, 2, 0
                )
                .init(GL_STATIC_DRAW)
                .setProgram(program)
        );

        binders.put("sky", new UniBinder()
                .setVertices(
                        1, 1, 1, 0.494f, 0.808f, 0.957f,
                        1, -1, 1, 0.494f, 0.808f, 0.957f,
                        -1, -1, 1, 0.494f, 0.808f, 0.957f,
                        -1, 1, 1, 0.494f, 0.808f, 0.957f
                )
                .setIndices(
                        0, 1, 2,
                        3, 2, 0
                )
                .init(GL_STATIC_DRAW)
                .setProgram(program)
        );

        var oscillator = new Oscillator(100000);


        //noinspection unused
        var trans = new Matrix4f();
        var eye = new Matrix4f();

//        var model = new Matrix4f().rotate((float) Math.toRadians(-55.0), 1, 0, 0);
        var model = new Matrix4f();

        int timestamp = (int) glfwGetTime();
        int count = 0;

        glEnable(GL_DEPTH_TEST);

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            if (changeMode) {
                System.out.println("change itemMode to " + itemMode);

                //Use explicit delete to save VRAM.
                if (uniBinder != null) {
                    uniBinder.delete();
                    uniBinder = null;
                }
                if (triBinder != null) {
                    triBinder.delete();
                    triBinder = null;
                }

                //hack to force binder update
                loader.setChanged(true);

                changeMode = false;
            }

            if (loader.hasChanged()) {
                System.out.println("reload because loader has changed");

                switch (itemMode) {
                    case UNI:
                        updateUniBinder(program);
                        break;
                    case TRI:
                        updateTriBinder(geoProgram);
                        break;
                }

                loader.setChanged(false);
            }

            //manage title
            int current = (int) glfwGetTime();
            if (current != timestamp) {
                timestamp = current;
                glfwSetWindowTitle(window, String.format("%dFPS @ %d sec", count, current));
                count = 0;
                System.out.println(timestamp);
            } else {
                count++;
            }

            //manage background
            float grey = oscillator.next();
            glClearColor(grey, grey, grey, 1.0f);
//            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            draw("one", model, new Matrix4f()
                    .rotate((float) (grey * Math.PI), 0, 0, 1)
                    .translate(0.2f, 0, 0)
            );

            draw("land", model, eye);
            draw("sky", model, eye);

            switch (itemMode) {
                case UNI:
                    draw(uniBinder, model, eye);
                    break;
                case TRI:
                    draw(triBinder, model, eye);
                    break;
            }

            glfwSwapBuffers(window);
        }

        System.out.println(glGetInteger(GL_MAX_VERTEX_ATTRIBS));

        glfwTerminate();
    }

    private void updateTriBinder(int program) {
        //Can use Stream to map, but I care about single thread performance.
        List<double[][]> triangles = loader.getTriangles();
        float[] data = new float[triangles.size() * 12];
        for (int tri = 0; tri < triangles.size(); tri++) {
            for (int ptr = 0; ptr < 3; ptr++) {
                for (int axis = 0; axis < 3; axis++) {
                    data[12 * tri + 3 * ptr + axis] = (float) triangles.get(tri)[ptr][axis] / metric;
                }
            }
            data[12 * tri + 9] = rand.nextFloat();
            data[12 * tri + 10] = rand.nextFloat();
            data[12 * tri + 11] = rand.nextFloat();
        }
        triBinder = new TriBinder().setData(data).init(GL_STATIC_DRAW).setProgram(program);
    }

    private void updateUniBinder(int program) {
        final List<double[]> ptrs = loader.getVertexes();
        float[] vertices = new float[ptrs.size() * 6];
        for (int k = 0; k < ptrs.size(); k++) {
            vertices[6 * k] = (float) ptrs.get(k)[0] / metric;
            vertices[6 * k + 1] = (float) ptrs.get(k)[1] / metric;
            vertices[6 * k + 2] = (float) ptrs.get(k)[2] / metric;
            vertices[6 * k + 3] = rand.nextFloat();
            vertices[6 * k + 4] = rand.nextFloat();
            vertices[6 * k + 5] = rand.nextFloat();
        }

        uniBinder = new UniBinder()
                .setVertices(vertices)
                .setIndices(loader.getShapes().stream().flatMapToInt(Arrays::stream).toArray())
                .init(GL_STATIC_DRAW)
                .setProgram(program);
    }
}
