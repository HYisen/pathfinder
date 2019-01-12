package net.alexhyisen.pathfinder.world;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private final float MAX_ZOOM = 120.0f;
    private Vector3f pos = new Vector3f(0, 0, 3);
    private Vector3f front = new Vector3f(0, 0, -1);
    private Vector3f up = new Vector3f(0, 1, 0);
    private Matrix4f view = null;
    private Matrix4f projection = null;
    private float speed = 1.0f;
    private float deltaTime = 0.0f;
    private float lastFrame = 0.0f;
    private boolean[] keys;
    private float lastX = 500.0f;
    private float lastY = 500.0f;
    private float sensitivity = 100.0f;
    private float yaw = -90.0f;
    private float pitch = 0.0f;
    private float zoom = 45.0f;


    public Camera(boolean[] keys) {
        this.keys = keys;
    }

    void handleScroll(long window, double xoffset, double yoffset) {
        if (zoom >= 1.0f && zoom <= MAX_ZOOM) {
            zoom -= yoffset;
            projection = null;
        }
        if (zoom < 1.0f) {
            zoom = 1.0f;
        } else if (zoom > MAX_ZOOM) {
            zoom = MAX_ZOOM;
        }
        System.out.println(yaw);
    }

    Matrix4f getView() {
        var currentFrame = glfwGetTime();
        deltaTime = (float) (currentFrame - lastFrame);
        lastFrame = (float) currentFrame;

        if (keys[GLFW_KEY_W]) {
            pos.add(new Vector3f(front).mul(speed * deltaTime));
            view = null;
        }
        if (keys[GLFW_KEY_S]) {
            pos.add(new Vector3f(front).mul(-speed * deltaTime));
            view = null;
        }
        if (keys[GLFW_KEY_A]) {
            pos.add(new Vector3f(front).cross(up).normalize().mul(-speed * deltaTime));
            view = null;
        }
        if (keys[GLFW_KEY_D]) {
            pos.add(new Vector3f(front).cross(up).normalize().mul(speed * deltaTime));
            view = null;
        }

        if (keys[GLFW_KEY_UP]) {
            pitch += sensitivity * deltaTime;
            front = null;
        }
        if (keys[GLFW_KEY_DOWN]) {
            pitch -= sensitivity * deltaTime;
            front = null;
        }
        if (keys[GLFW_KEY_LEFT]) {
            yaw += sensitivity * deltaTime;
            front = null;
        }
        if (keys[GLFW_KEY_RIGHT]) {
            yaw -= sensitivity * deltaTime;
            front = null;
        }
        while (yaw > 360.0f) {
            yaw -= 360.0f;
        }
        while (yaw < -360.0f) {
            yaw += 360.0f;
        }
        if (pitch > 89.0f) {
            pitch = 89.0f;
        } else if (pitch < -89.0f) {
            pitch = -89.0f;
        }

        if (front == null) {
            front = new Vector3f(
                    (float) Math.sin(Math.toRadians(pitch) * Math.cos(Math.toRadians(yaw))),
                    (float) Math.sin(Math.toRadians(pitch)),
                    (float) Math.cos(Math.toRadians(pitch) * Math.sin(Math.toRadians(yaw)))
            ).normalize();
            view = null;
            System.out.println(pitch + " " + yaw);
        }

        if (view == null) {
            view = new Matrix4f().lookAt(pos, new Vector3f(pos).add(front), up);
        }
        return view;
    }

    Matrix4f getProjection() {
        if (projection == null) {
            projection = new Matrix4f().perspective((float) Math.toRadians(zoom),
                    1, 0.1f, 100.0f);
        }
        return projection;
    }
}
