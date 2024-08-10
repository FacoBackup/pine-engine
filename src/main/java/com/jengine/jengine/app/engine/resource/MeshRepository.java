package com.jengine.jengine.app.engine.resource;

import com.jengine.jengine.app.engine.resource.mesh.Mesh;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class MeshRepository {
    private final Map<String, Mesh> meshMap = new HashMap<>();

    Mesh addMesh(float[] vertices, int[] indices, @Nullable float[] normals, @Nullable float[] uvs) {
        String id = UUID.randomUUID().toString();
        return meshMap.put(id, new Mesh(id, vertices, indices, normals, uvs));
    }

    void removeMesh(String id) {
        meshMap.remove(id);
    }

    void removeMesh(Mesh mesh) {
        meshMap.remove(mesh.getId());
    }

    Collection<Mesh> getMeshes() {
        return meshMap.values();
    }
}
