package com.pine.service.importer.impl.mesh;

import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.texture.TextureStreamableResource;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.lwjgl.assimp.Assimp.*;

public class MeshImporterUtil {

    public static CoreMeshData processPrimitive(AIMesh mesh) {
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
        return new CoreMeshData(vertices, indices, normals, uvs);
    }

    public static void traverseAssimpTree(AINode node, AssimpMeshRepresentation parent, Map<Integer, List<AssimpMeshRepresentation>> byPrimitive) {
        var newInstance = new AssimpMeshRepresentation(parent);
        newInstance.name = node.mName().dataString();

        parent.children.add(newInstance);
        if (node.mNumMeshes() > 0) {
            for (int i = 0; i < node.mNumMeshes(); i++) {
                newInstance.mesh = node.mMeshes().get(i);
                byPrimitive.putIfAbsent(newInstance.mesh, new ArrayList<>());
                byPrimitive.get(newInstance.mesh).add(newInstance);
            }
        }

        for (int i = 0; i < node.mNumChildren(); i++) {
            AINode child = AINode.create(node.mChildren().get(i));
            traverseAssimpTree(child, newInstance, byPrimitive);
        }
    }

    public static List<MaterialTextureData> processMaterial(AIMaterial material, int i, String sceneName) {
        List<MaterialTextureData> allTexture = new ArrayList<>();

        getMaterial(material, aiTextureType_HEIGHT, i, sceneName, allTexture);
        getMaterial(material, aiTextureType_NORMALS, i, sceneName, allTexture);
        getMaterial(material, aiTextureType_BASE_COLOR, i, sceneName, allTexture);
        getMaterial(material, aiTextureType_METALNESS, i, sceneName, allTexture);
        getMaterial(material, aiTextureType_DIFFUSE_ROUGHNESS, i, sceneName, allTexture);
        getMaterial(material, aiTextureType_AMBIENT_OCCLUSION, i, sceneName, allTexture);

        return allTexture;
    }

    private static void getMaterial(AIMaterial material, int type, int i, String sceneName, List<MaterialTextureData> allTexture) {
        AIString path = AIString.calloc();
        if (aiGetMaterialTexture(material, type, 0, path, (IntBuffer) null, null, null, null, null, null) == aiReturn_SUCCESS) {
            allTexture.add(new MaterialTextureData(path.dataString(), sceneName + type + "-texture" + i, type));
        }
    }
}
