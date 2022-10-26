package me.petrolingus.rpc.lwjgl.camera;

import me.petrolingus.rpc.util.MouseInput;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private float phi;
    private float theta;

    private Vector3f eye;

    private static final Vector3f ZERO = new Vector3f().zero();
    private static final Vector3f UP = new Vector3f(0, -1, 0);

    public Camera() {
        this(45, 45);
    }

    public Camera(float phi, float theta) {
        this.phi = phi;
        this.theta = theta;
        this.eye = new Vector3f().zero();
    }

    public void input(MouseInput mouseInput) {

        mouseInput.input();

        theta -= mouseInput.getDisplVec().x * 0.3;
        if (theta < 0 || theta > 160) {
            theta += mouseInput.getDisplVec().x * 0.3;
        }

        phi -= mouseInput.getDisplVec().y * 0.3;
    }

    public Matrix4f getViewMatrix() {

        float eyeX = (float) (2 * Math.sin(Math.toRadians(theta)) * Math.cos(Math.toRadians(phi)));
        float eyeY = (float) (2 * Math.sin(Math.toRadians(theta)) * Math.sin(Math.toRadians(phi)));
        float eyeZ = (float) (2 * Math.cos(Math.toRadians(theta)));
        eye.set(eyeX, eyeZ, eyeY);

        return new Matrix4f().setLookAt(eye, ZERO, UP);
    }
}
