package me.petrolingus.swi.lwjgl.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class Camera {

    private float phi;
    private float tau;
    private float zoom;
    private boolean isTop;

    public Camera() {
        this(45, 45, 10);
    }

    public Camera(float phi, float tau, float zoom) {
        this.phi = phi;
        this.tau = tau;
        this.zoom = zoom;
    }

    public void input(long window) {

        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            phi += 0.5;
        } else if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            phi -= 0.5;
        }

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            tau -= 0.5;
            if (tau < 30) {
                tau = 30;
            }
        } else if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            tau += 0.5;
            if (tau > 150) {
                tau = 150;
            }
        }

        if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS) {
            tau = 180;
            phi = 270;
            isTop = true;
        } else if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) {
            tau = 45;
            phi = 45;
            isTop = false;
        }

    }

    public Matrix4f getViewMatrix() {

        float eyeX = zoom * (float) Math.sin(Math.toRadians(tau)) * (float) Math.cos(Math.toRadians(phi));
        float eyeY = zoom * (float) Math.sin(Math.toRadians(tau)) * (float) Math.sin(Math.toRadians(phi));
        float eyeZ = zoom * (float) Math.cos(Math.toRadians(tau));

        Vector3f eye = new Vector3f(eyeX, eyeZ, eyeY);
        Vector3f center = new Vector3f(0, 0, 0);
        Vector3f up = new Vector3f(0, -1, 0);
        return new Matrix4f().setLookAt(eye, center, up);
    }
}
