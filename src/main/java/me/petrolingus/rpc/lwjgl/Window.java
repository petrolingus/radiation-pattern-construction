package me.petrolingus.rpc.lwjgl;

import me.petrolingus.rpc.Controller;
import me.petrolingus.rpc.lwjgl.axis.Axis;
import me.petrolingus.rpc.lwjgl.camera.Camera;
import me.petrolingus.rpc.lwjgl.mesh.Mesh;
import me.petrolingus.rpc.lwjgl.mesh.MeshGenerator;
import me.petrolingus.rpc.util.MouseInput;
import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.nio.ByteBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static final int WIDTH = 720;
    private static final int HEIGHT = 720;

    public static long window;

    public ByteBuffer buffer;

    public MouseInput mouseInput;

    public void run() throws Exception {

        buffer = BufferUtils.createByteBuffer(WIDTH * HEIGHT * 4);

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void init() {

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_STENCIL_BITS, 4);
        glfwWindowHint(GLFW_SAMPLES, 4);

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwHideWindow(window);
    }

    private void loop() throws Exception {

        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glClearColor(0, 0, 0, 1);
//        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        String fragmentShaderPath = "fragment.shader";
        String vertexShaderPath = "vertex.shader";
        ShaderProgram shaderProgram = new ShaderProgram(vertexShaderPath, fragmentShaderPath);
        shaderProgram.createUniform("viewMatrix");
        shaderProgram.createUniform("projectionMatrix");

        Matrix4f projectionMatrix = new Matrix4f().setOrtho(-WIDTH / 2.0f, WIDTH / 2.0f, HEIGHT / 2.0f, -HEIGHT / 2.0f, 0.01f, 1000.f, true);
        Camera camera = new Camera();

        Mesh mesh = MeshGenerator.generate(512);
        Axis axis = new Axis();

        float[] positions = mesh.getPositions();

        while (!glfwWindowShouldClose(window)) {

            if (Controller.isChanged) {

                double lambda = Controller.lambda;
                double d = Controller.omega * lambda;
                double R = Controller.radius;

                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;

                double t1 = 0;
                double t2 = 0;

                for (int i = 1; i < positions.length; i += 3) {
                    double a = 0;
                    double b = 0;
                    for (int j = 0; j < 10; j++) {
                        for (int k = 0; k < 10; k++) {
                            if (Controller.sources[j][k]) {
                                double x1 = R * positions[i - 1] * d;
                                double y1 = R * positions[i + 1] * d;
                                double z1 = Math.sqrt(R * R - x1 * x1 - y1 * y1);
                                double x2 = j * d - 5 * d + 0.5 * d;
                                double y2 = k * d - 5 * d + 0.5 * d;
                                t1 = x2;
                                t2 = y2;
                                double z2 = 0;
                                double dx = x1 - x2;
                                double dy = y1 - y2;
                                double dz = z1 - z2;
                                double r = Math.sqrt(dx * dx + dy * dy + dz * dz);
                                a += Math.cos(2.0 * Math.PI * r / lambda) / r;
                                b += Math.sin(2.0 * Math.PI * r / lambda) / r;
                            }
                        }
                    }
                    double value = Math.sqrt(a * a + b * b);
                    max = Math.max(max, value);
                    min = Math.min(min, value);
                    positions[i] = (float) value;
                }

                System.out.println(t1 + ":" + t2);

                for (int i = 1; i < positions.length; i += 3) {
                    positions[i] = (float) ((positions[i] - min) / (max - min)) - 0.5f;
                }

                Controller.isChanged = false;
            }

            camera.input(mouseInput);
            Matrix4f viewMatrix = camera.getViewMatrix();
            viewMatrix.scale(HEIGHT / 3.0f);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            axis.draw(viewMatrix, projectionMatrix);

            shaderProgram.bind();
            shaderProgram.setUniform("viewMatrix", viewMatrix);
            shaderProgram.setUniform("projectionMatrix", projectionMatrix);
            mesh.render();
            shaderProgram.unbind();

            glReadPixels(0, 0, WIDTH, HEIGHT, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        }
    }

}
