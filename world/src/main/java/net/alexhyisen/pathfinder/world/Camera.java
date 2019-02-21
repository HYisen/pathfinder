package net.alexhyisen.pathfinder.world;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private Vector3f pos = new Vector3f(0, 0, 3);
    private Vector3f front = new Vector3f(0, 0, -1);
    private Vector3f up = new Vector3f(0, 1, 0);
    private final float MAX_ZOOM = 120.0f;

    private Matrix4f view = null;
    private Matrix4f projection = null;

    private float speed = 1.0f;
    private float deltaTime = 0.0f;
    private float lastFrame = 0.0f;
    private boolean[] keys;
    private float lastX = 500.0f;
    private float lastY = 500.0f;
    private Vector3f worldUp = new Vector3f(0, 1, 0);
    private float sensitivity = 0.05f;
    private float yaw = -90.0f;
    private float pitch = 0.0f;
    private float zoom = 45.0f;
    private float sensitivity0 = 100f;
    private boolean firstMove = true;


    public Camera(boolean[] keys) {
        this.keys = keys;
    }

    void handleCursor(long window, double xpos, double ypos) {
        if (firstMove) {
            firstMove = false;
            lastX = (float) xpos;
            lastY = (float) ypos;
        }
        float xoffset = (float) (lastX - xpos);
        float yoffset = (float) (lastY - ypos);
        lastX = (float) xpos;
        lastY = (float) ypos;

        rotate(yoffset * sensitivity, xoffset * sensitivity);
    }

    private void rotate(float pitchOffset, float yawOffset) {
        yaw += yawOffset;
        while (yaw > 360.0f) {
            yaw -= 360.0f;
        }
        while (yaw < -360.0f) {
            yaw += 360.0f;
        }
        pitch += pitchOffset;
        if (pitch > 89.0f) {
            pitch = 89.0f;
        } else if (pitch < -89.0f) {
            pitch = -89.0f;
        }

        System.out.println(String.format("yaw=%f pitch=%f", yaw, pitch));

        float cosYaw = (float) Math.cos(Math.toRadians(yaw));
        float sinYaw = (float) Math.sin(Math.toRadians(yaw));
        float cosPitch = (float) Math.cos(Math.toRadians(pitch));
        float sinPitch = (float) Math.sin(Math.toRadians(pitch));

        front.set(
                sinYaw * cosPitch,
                sinPitch,
                cosYaw * cosPitch
        ).normalize();

        up = new Vector3f(front).cross(worldUp).normalize().cross(front).normalize();
        view = null;
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
        if (keys[GLFW_KEY_F]) {
            pos.y += speed * deltaTime;
            view = null;
        }
        if (keys[GLFW_KEY_C]) {
            pos.y -= speed * deltaTime;
            view = null;
        }
//        pos.y = 0;

        if (keys[GLFW_KEY_UP]) {
            rotate(sensitivity0 * deltaTime, 0);
        }
        if (keys[GLFW_KEY_DOWN]) {
            rotate(-sensitivity0 * deltaTime, 0);

        }
        if (keys[GLFW_KEY_LEFT]) {
            rotate(0, sensitivity0 * deltaTime);

        }
        if (keys[GLFW_KEY_RIGHT]) {
            rotate(0, -sensitivity0 * deltaTime);

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

    void print() {
        System.out.println(pos);
    }
}
