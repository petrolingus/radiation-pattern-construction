package me.petrolingus.swi.lwjgl;

import me.petrolingus.swi.util.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private final int programId;

    private final Map<String, Integer> uniforms;

    public ShaderProgram(String vertexShaderPath, String fragmentShaderPath) throws Exception {
        programId = GL30.glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
        uniforms = new HashMap<>();
        createShaders(vertexShaderPath, fragmentShaderPath);
    }

    private void createShaders(String vertexShaderPath, String fragmentShaderPath) throws Exception {

        // Create vertex shader
        int vertexShaderId = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
        if (vertexShaderId == 0) {
            throw new Exception("Error creating shader. Type: " + GL30.GL_VERTEX_SHADER);
        }
        compileShader(vertexShaderPath, vertexShaderId);

        // Create fragment shader
        int fragmentShaderId = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
        if (fragmentShaderId == 0) {
            throw new Exception("Error creating shader. Type: " + GL30.GL_FRAGMENT_SHADER);
        }
        compileShader(fragmentShaderPath, fragmentShaderId);

        // Link shaders to program
        GL30.glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking shader code: " + glGetProgramInfoLog(programId, 1024));
        }
        GL30.glDetachShader(programId, vertexShaderId);
        GL30.glDetachShader(programId, fragmentShaderId);
        GL30.glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating shader code: " + glGetProgramInfoLog(programId, 1024));
        }
    }

    private void compileShader(String shaderPath, int shaderId) throws Exception {
        String fragmentShaderCode = Utils.loadShader(shaderPath);
        glShaderSource(shaderId, fragmentShaderCode);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }
        glAttachShader(programId, shaderId);
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform:" + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, float value) {
        GL30.glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f value) {
        GL30.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL30.glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }
}

