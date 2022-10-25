package me.petrolingus.rpc.lwjgl.axis;

import me.petrolingus.rpc.lwjgl.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class Axis {

    private final ShaderProgram shaderProgram;

    public Axis() throws Exception {

        String fragmentShader = "axis/fragment.shader";
        String vertexShader = "axis/vertex.shader";
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        shaderProgram.createUniform("viewMatrix");
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("color");
    }

    public void draw(final Matrix4f viewMatrix, final Matrix4f projectionMatrix) {

        shaderProgram.bind();

        shaderProgram.setUniform("viewMatrix", viewMatrix);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        shaderProgram.setUniform("color", new Vector3f(1.0f, 0.0f, 0.0f));
        glBegin(GL_LINES);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(10.0f, 0.0f, 0.0f);
        glEnd();

        shaderProgram.setUniform("color", new Vector3f(0.0f, 1.0f, 0.0f));
        glBegin(GL_LINES);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 10.0f, 0.0f);
        glEnd();

        shaderProgram.setUniform("color", new Vector3f(0.0f, 0.0f, 1.0f));
        glBegin(GL_LINES);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 10.0f);
        glEnd();

        shaderProgram.unbind();
    }

}
