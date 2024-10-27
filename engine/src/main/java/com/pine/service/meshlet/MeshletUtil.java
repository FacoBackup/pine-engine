package com.pine.service.meshlet;

import com.pine.service.importer.data.MeshImportData;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.meshoptimizer.MeshOptimizer;
import org.lwjgl.util.meshoptimizer.MeshoptMeshlet;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.memAddress;

public class MeshletUtil {
    private static final long MAX_VERTICES = 64;
    private static final long MAX_TRIANGLES = 124;

    public static Meshlets genMeshlets(MeshImportData data) {
//        System.setProperty("org.lwjgl.util.NoChecks", "true");

        long maxMeshlets = MeshOptimizer.meshopt_buildMeshletsBound(data.indices.length, MAX_VERTICES, MAX_TRIANGLES);

        // Allocate space for output meshlets and vertex remap
        MeshoptMeshlet.Buffer meshlets = MeshoptMeshlet.create((int) maxMeshlets);
        IntBuffer meshletVertices = MemoryUtil.memAllocInt((int) (maxMeshlets * MAX_VERTICES));
        ByteBuffer meshletTriangles = MemoryUtil.memAlloc((int) (maxMeshlets * MAX_TRIANGLES * 3));

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(data.indices.length);
        for (var i : data.indices) {
            indicesBuffer.put(i);
        }

        indicesBuffer.position(0);
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(data.vertices.length);
        for (var i : data.vertices) {
            vertexBuffer.put(i);
        }
        vertexBuffer.position(0);

        long meshletCount = MeshOptimizer.nmeshopt_buildMeshlets(meshlets.address(),
                memAddress(meshletVertices),
                memAddress(meshletTriangles),
                memAddress(indicesBuffer),
                data.indices.length,
                memAddress(vertexBuffer),
                data.vertices.length / 3,
                3 * Float.BYTES,
                MAX_VERTICES,
                MAX_TRIANGLES,
                .5f
        );

        List<MeshletInfo> meshletList = new ArrayList<>();
        for (int i = 0; i < meshletCount; i++) {
            MeshoptMeshlet meshlet = meshlets.get(i);

            // Collect vertices for the meshlet
            int vertexIndexStart = meshlet.vertex_offset();
            int vertexIndexLength = meshlet.vertex_count();

            int trianglesStart = meshlet.triangle_offset() * 3;
            int trianglesLength = meshlet.triangle_count() * 3;

            meshletList.add(new MeshletInfo(
                    vertexIndexStart,
                    vertexIndexLength,
                    trianglesStart,
                    trianglesLength
            ));
        }

        MemoryUtil.memFree(indicesBuffer);
        // Clean up
        meshlets.free();
        return new Meshlets(meshletList, vertexBuffer, meshletVertices, meshletTriangles);
    }
}
