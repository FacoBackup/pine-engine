package com.pine.service.loader.impl;

import com.pine.injection.EngineDependency;
import com.pine.injection.EngineInjectable;
import com.pine.service.loader.AbstractResourceLoader;
import com.pine.service.loader.impl.info.AbstractLoaderExtraInfo;
import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.loader.impl.info.MeshLoaderExtraInfo;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import com.pine.service.loader.impl.response.MeshInstanceMetadata;
import com.pine.service.loader.impl.response.MeshLoaderResponse;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.primitives.mesh.MeshCreationData;
import com.pine.service.resource.resource.ResourceType;
import jakarta.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EngineInjectable
public class MeshLoader extends AbstractResourceLoader {

    @EngineDependency
    public ResourceService resources;

    @Override
    public AbstractLoaderResponse load(LoadRequest resource, @Nullable AbstractLoaderExtraInfo extraInfo) {
        var extra = (MeshLoaderExtraInfo) extraInfo;
        try {
            List<MeshInstanceMetadata> meshes = new ArrayList<>();
            AIScene scene = loadScene(resource);

            if (scene == null) {
                getLogger().error("Failed to load mesh at {}", resource.path());
                return new MeshLoaderResponse(false, resource.path(), Collections.emptyList());
            }

            PointerBuffer meshList = scene.mMeshes();
            if (meshList != null) {
                for (int i = 0; i < scene.mNumMeshes(); i++) {
                    AIMesh mesh = AIMesh.create(meshList.get(i));
                    meshes.add(processPrimitive(mesh, resource, i, extra));
                }
            }

            if(extra != null && extra.isInstantiateHierarchy()){
                // TODO - INSTANTIATE HIERARCHY
            }

            Assimp.aiReleaseImport(scene);
            return new MeshLoaderResponse(true, resource.path(), meshes);
        } catch (Exception e) {
            return new MeshLoaderResponse(false, resource.path(), Collections.emptyList());
        }
    }

    @Nullable
    private AIScene loadScene(LoadRequest resource) {
        AIScene scene = null;
        if (resource.isStaticResource()) {
            ByteBuffer byteBuffer = loadStaticResource(resource.path());
            if (byteBuffer != null) {
                scene = Assimp.aiImportFileFromMemory(byteBuffer, Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs, (CharBuffer) null);
            }
        } else {
            scene = Assimp.aiImportFile(resource.path(), Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs);
        }
        return scene;
    }

    private MeshInstanceMetadata processPrimitive(AIMesh mesh, LoadRequest resource, int index, @Nullable MeshLoaderExtraInfo extra) {
        String resourceId = null;
        if (extra == null || extra.getMeshIndex() == null || extra.getMeshIndex() == index) {
            float[] vertices = new float[mesh.mNumVertices() * 3];
            for (int i = 0; i < mesh.mNumVertices(); i++) {
                AIVector3D vert = mesh.mVertices().get(i);
                vertices[i * 3] = vert.x();
                vertices[i * 3 + 1] = vert.y();
                vertices[i * 3 + 2] = vert.z();
            }

            int[] indices = new int[mesh.mNumFaces() * 3];
            for (int i = 0; i < mesh.mNumFaces(); i++) {
                AIFace face = mesh.mFaces().get(i);
                indices[i * 3] = face.mIndices().get(0);
                indices[i * 3 + 1] = face.mIndices().get(1);
                indices[i * 3 + 2] = face.mIndices().get(2);
            }

            float[] normals = null;
            AIVector3D.Buffer mNormals = mesh.mNormals();
            if (mNormals != null) {
                normals = new float[mesh.mNumVertices() * 3];
                for (int i = 0; i < mesh.mNumVertices(); i++) {
                    AIVector3D normal = mNormals.get(i);
                    normals[i * 3] = normal.x();
                    normals[i * 3 + 1] = normal.y();
                    normals[i * 3 + 2] = normal.z();
                }
            }

            float[] uvs = null;
            AIVector3D.Buffer nUV = mesh.mTextureCoords(0);
            if (nUV != null) {
                uvs = new float[mesh.mNumVertices() * 2];
                for (int i = 0; i < mesh.mNumVertices(); i++) {
                    uvs[i * 2] = nUV.get(i).x();
                    uvs[i * 2 + 1] = nUV.get(i).y();
                }
            }
            resourceId = resources.addResource(new MeshCreationData(vertices, indices, normals, uvs)).getId();
        }
        return new MeshInstanceMetadata(mesh.mName().dataString(), resource.path(), index, resourceId);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.MESH;
    }
}
