package com.jengine.app.core.resource;

import com.jengine.app.IResource;
import com.jengine.app.ResourceRuntimeException;
import com.jengine.app.core.resource.mesh.Mesh;
import com.jengine.app.core.resource.mesh.StaticMesh;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class MeshRepository implements IResource {

    private final Map<StaticMesh, Mesh> staticMeshes = new HashMap<>();
    private final Map<String, Mesh> meshMap = new HashMap<>();

    class RawStaticMesh {
        public float[] vertices;
        public int[] indices;
        public float[] normals;
        public float[] uvs;
    }

    class RawStaticMeshes {
        public RawStaticMesh QUAD;
        public RawStaticMesh SPHERE;
        public RawStaticMesh CUBE;
        public RawStaticMesh CYLINDER;
        public RawStaticMesh PLANE;
        public RawStaticMesh CUBE_LINEAR;
    }

    public void loadAll() throws ResourceRuntimeException {
        RawStaticMeshes rawStaticMeshes = GSON.fromJson(new String(loadFromResources("STATIC_MESHES.json")), RawStaticMeshes.class);
        staticMeshes.put(StaticMesh.QUAD, new Mesh(StaticMesh.QUAD.name(), rawStaticMeshes.QUAD.vertices, rawStaticMeshes.QUAD.indices, rawStaticMeshes.QUAD.normals, rawStaticMeshes.QUAD.uvs));
        staticMeshes.put(StaticMesh.SPHERE, new Mesh(StaticMesh.SPHERE.name(), rawStaticMeshes.SPHERE.vertices, rawStaticMeshes.SPHERE.indices, rawStaticMeshes.SPHERE.normals, rawStaticMeshes.SPHERE.uvs));
        staticMeshes.put(StaticMesh.CUBE, new Mesh(StaticMesh.CUBE.name(), rawStaticMeshes.CUBE.vertices, rawStaticMeshes.CUBE.indices, rawStaticMeshes.CUBE.normals, rawStaticMeshes.CUBE.uvs));
        staticMeshes.put(StaticMesh.CYLINDER, new Mesh(StaticMesh.CYLINDER.name(), rawStaticMeshes.CYLINDER.vertices, rawStaticMeshes.CYLINDER.indices, rawStaticMeshes.CYLINDER.normals, rawStaticMeshes.CYLINDER.uvs));
        staticMeshes.put(StaticMesh.PLANE, new Mesh(StaticMesh.PLANE.name(), rawStaticMeshes.PLANE.vertices, rawStaticMeshes.PLANE.indices, rawStaticMeshes.PLANE.normals, rawStaticMeshes.PLANE.uvs));
        staticMeshes.put(StaticMesh.CUBE_LINEAR, new Mesh(StaticMesh.CUBE_LINEAR.name(), rawStaticMeshes.CUBE_LINEAR.vertices, rawStaticMeshes.CUBE_LINEAR.indices, rawStaticMeshes.CUBE_LINEAR.normals, rawStaticMeshes.CUBE_LINEAR.uvs));
    }

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
