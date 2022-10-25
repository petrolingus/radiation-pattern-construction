package me.petrolingus.rpc.lwjgl.mesh;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh {

    private final float[] positions;
    private final int vertexCount;
    private final int vaoId;
    private final int positionsVboId;

    public Mesh(float[] positions, int[] indices) {

        this.positions = positions;

        vertexCount = indices.length;
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        positionsVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, positionsVboId);
        glBufferData(GL_ARRAY_BUFFER, positions, GL_STREAM_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
//        glBindBuffer(GL_ARRAY_BUFFER, 0);

        int indicesVboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render() {

        positionsUpdate();

        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void positionsUpdate() {
        glBindBuffer(GL_ARRAY_BUFFER, positionsVboId);
        glBufferData(GL_ARRAY_BUFFER, positions, GL_STREAM_DRAW);
    }

    public float[] getPositions() {
        return positions;
    }

}
