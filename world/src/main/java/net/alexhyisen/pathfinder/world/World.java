package net.alexhyisen.pathfinder.world;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class World {
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

    private static int loadShader(String name, int type) {
        var shader = glCreateShader(type);
        glShaderSource(shader, loadShaderCode(name));
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException(glGetShaderInfoLog(shader));
        }
        return shader;
    }

    private static int genProgram(String vertexShaderName, String fragmentShaderName) {
        var vertexShader = loadShader(vertexShaderName, GL_VERTEX_SHADER);
        var fragmentShader = loadShader(fragmentShaderName, GL_FRAGMENT_SHADER);

        var program = glCreateProgram();

        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);

        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
            throw new RuntimeException(glGetProgramInfoLog(program));
        }

        return program;
    }

    public static void main(String[] args) {
        var keys = new boolean[1024];
        var camera = new Camera(keys);

        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        long window = glfwCreateWindow(1000, 1000, "World", 0, 0);
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
                    default:
                        break;
                }
            } else if (action == GLFW_RELEASE) {
                keys[key] = false;
            }
        });

//        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
//        glfwSetCursorPosCallback(window, camera::handleCursor);

        glfwSetScrollCallback(window, camera::handleScroll);

        GL.createCapabilities();

        var one = new Binder()
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
                .init(GL_STATIC_DRAW);

        var land = new Binder()
                .setVertices(
                        1, 1, 0.0f, 1f, 1f, 1f,
                        1, -1, 0.0f, 1f, 1f, 1f,
                        -1, -1, 0.0f, 1f, 1f, 1f,
                        -1, 1, 0.0f, 1f, 1f, 1f
                )
                .setIndices(
                        0, 1, 2,
                        3, 2, 0
                )
                .init(GL_STATIC_DRAW);

        var sky = new Binder()
                .setVertices(
                        1, 1, 1, 0, 0, 0,
                        1, -1, 1, 0, 0, 0,
                        -1, -1, 1, 0, 0, 0,
                        -1, 1, 1, 0, 0, 0
                )
                .setIndices(
                        0, 1, 2,
                        3, 2, 0
                )
                .init(GL_STATIC_DRAW);

        var program = genProgram("one", "two");

        var oscillator = new Oscillator(100000);

        FloatBuffer[] fb = Stream
                .generate(() -> BufferUtils.createFloatBuffer(16))
                .limit(4)
                .toArray(FloatBuffer[]::new);

        var trans = new Matrix4f();
        final Matrix4f eye = new Matrix4f();

        var model = new Matrix4f().rotate((float) Math.toRadians(-55.0), 1, 0, 0);

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            float grey = oscillator.next();
            glClearColor(grey, grey, grey, 1.0f);
//            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glUseProgram(program);
//            grey = grey - 0.5f;
//            glUniform3f(glGetUniformLocation(program, "bias"), 0, grey, 0);
            glBindVertexArray(one.getVao());
            trans.set(eye);
            glUniformMatrix4fv(glGetUniformLocation(program, "transform"), false,
                    trans
                            .rotate((float) (grey * Math.PI), 0, 0, 1)
                            .translate(0.2f, 0, 0)
                            .get(fb[0]));
            glUniformMatrix4fv(glGetUniformLocation(program, "model"), false,
                    model.get(fb[1]));
            glUniformMatrix4fv(glGetUniformLocation(program, "view"), false,
                    camera.getView().get(fb[2]));
            glUniformMatrix4fv(glGetUniformLocation(program, "projection"), false,
                    camera.getProjection().get(fb[3]));

            glDrawElements(GL_TRIANGLES, 12, GL_UNSIGNED_INT, 0);
            glBindVertexArray(0);

            glBindVertexArray(land.getVao());
            trans.set(eye);
            glUniformMatrix4fv(glGetUniformLocation(program, "transform"), false,
                    eye.get(fb[0]));
            glUniformMatrix4fv(glGetUniformLocation(program, "model"), false,
                    model.get(fb[1]));
            glUniformMatrix4fv(glGetUniformLocation(program, "view"), false,
                    camera.getView().get(fb[2]));
            glUniformMatrix4fv(glGetUniformLocation(program, "projection"), false,
                    camera.getProjection().get(fb[3]));

            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            glBindVertexArray(0);

            glBindVertexArray(sky.getVao());
            trans.set(eye);
            glUniformMatrix4fv(glGetUniformLocation(program, "transform"), false,
                    eye.get(fb[0]));
            glUniformMatrix4fv(glGetUniformLocation(program, "model"), false,
                    model.get(fb[1]));
            glUniformMatrix4fv(glGetUniformLocation(program, "view"), false,
                    camera.getView().get(fb[2]));
            glUniformMatrix4fv(glGetUniformLocation(program, "projection"), false,
                    camera.getProjection().get(fb[3]));

            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            glBindVertexArray(0);

            glfwSwapBuffers(window);
        }

        System.out.println(glGetInteger(GL_MAX_VERTEX_ATTRIBS));

        glfwTerminate();
    }
}
