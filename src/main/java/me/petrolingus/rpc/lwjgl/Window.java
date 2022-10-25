package me.petrolingus.rpc.lwjgl;

import me.petrolingus.rpc.Controller;
import me.petrolingus.rpc.lwjgl.axis.Axis;
import me.petrolingus.rpc.lwjgl.camera.Camera;
import me.petrolingus.rpc.lwjgl.mesh.Mesh;
import me.petrolingus.rpc.lwjgl.mesh.MeshGenerator;
import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static final int WIDTH = 720;
    private static final int HEIGHT = 720;

    public static long window;

    public void run() throws Exception {

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
        glfwShowWindow(window);
    }

    private void loop() throws Exception {

        GL.createCapabilities();
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glEnable(GL_STENCIL_TEST);
        GL11.glClearColor(0, 0, 0, 1);
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

                double d = Controller.sliderValue1; // 0.1
                double omega = Controller.sliderValue2; // 2
                double amplitude = Controller.sliderValue3; // 0.01

                float min = Float.MAX_VALUE;
                float max = Float.MIN_VALUE;

                for (int i = 1; i < positions.length; i += 3) {
                    double value = 0;
                    for (int j = 0; j < 10; j++) {
                        for (int k = 0; k < 10; k++) {
                            if (Controller.sources[j][k]) {
                                double x1 = 100 * positions[i - 1] * d;
                                double y1 = 100 * positions[i + 1] * d;
                                double x2 = j * d - 5 * d + 0.5 * d;
                                double y2 = k * d - 4 * d + 0.5 * d;
                                double dx = x1 - x2;
                                double dy = y1 - y2;
                                double r = Math.sqrt(dx * dx + dy * dy);
                                double a = (amplitude * Math.cos(omega * r) / r);
                                double b = Math.sin(omega * r);
                                double intensity = a * a + b * b;
                                value += intensity;
                            }
                        }
                    }
                    if (value > max) {
                        max = (float) value;
                    }
                    if (value < min) {
                        min = (float) value;
                    }
                    positions[i] = (float) value;
                }

                for (int i = 1; i < positions.length; i += 3) {
                    positions[i] = ((positions[i] - min) / (max - min)) - 0.5f;
                }

                Controller.isChanged = false;
            }

            camera.input(window);
            Matrix4f viewMatrix = camera.getViewMatrix();
            viewMatrix.scale(HEIGHT / 3.0f);

            GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            axis.draw(viewMatrix, projectionMatrix);

            shaderProgram.bind();
            shaderProgram.setUniform("viewMatrix", viewMatrix);
            shaderProgram.setUniform("projectionMatrix", projectionMatrix);
            mesh.render();
            shaderProgram.unbind();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

}
