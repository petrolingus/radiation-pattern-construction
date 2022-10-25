package me.petrolingus.rpc.lwjgl.mesh;

import java.util.ArrayList;
import java.util.List;

public class MeshGenerator {

    public static Mesh generate(int quality) {

        float h = 2.0f / quality;

        List<Float> positionsList = new ArrayList<>();

        for (int i = 0; i < quality + 1; i++) {
            float y = -1f + i * h;
            for (int j = 0; j < quality + 1; j++) {
                float x = -1f + j * h;
                positionsList.add(x);
                positionsList.add(0.0f);
                positionsList.add(y);
//                int id = i * (quality + 1) + j;
//                System.out.println("Vertex #" + id + ": (" + x + ", " + 0.0 + ", " + y + ")");
            }
        }

        float[] positions = new float[positionsList.size()];
        for (int i = 0; i < positionsList.size(); i++) {
            positions[i] = positionsList.get(i);
        }

        List<Integer> indicesList = new ArrayList<>();
        for (int i = 0; i < quality; i++) {
            for (int j = 0; j < quality; j++) {
                int id = i * quality + j + (i);
                indicesList.add(id); // 0
                indicesList.add(id + 1); // 1
                indicesList.add(id + quality + 2); // 3
                indicesList.add(id + quality + 2); // 3
                indicesList.add(id + quality + 1); // 1
                indicesList.add(id); // 0
            }
        }

        int[] indices = new int[indicesList.size()];
        for (int i = 0; i < indicesList.size(); i++) {
            indices[i] = indicesList.get(i);
//            System.out.println(indices[i]);
        }

        System.out.println("Vertices count: " + positions.length);
        System.out.println("Indices count: " + indices.length);

        return new Mesh(positions, indices);
    }

    public static Mesh generateSimple() {

        float[] positions = {
                -0.5f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f
        };

        int[] indices = {
                0, 1, 2, 2, 3, 0
        };

        return new Mesh(positions, indices);
    }
}
