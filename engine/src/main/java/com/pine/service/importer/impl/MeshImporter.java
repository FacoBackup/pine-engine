package com.pine.service.importer.impl;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.AbstractImporter;
import com.pine.service.importer.data.AbstractImportData;
import com.pine.service.importer.data.MaterialImportData;
import com.pine.service.importer.data.MeshImportData;
import com.pine.service.importer.data.SceneImportData;
import com.pine.service.importer.impl.mesh.AssimpMeshRepresentation;
import com.pine.service.importer.impl.mesh.MaterialTextureData;
import com.pine.service.importer.impl.mesh.MeshImporterUtil;
import com.pine.service.importer.metadata.AbstractResourceMetadata;
import com.pine.service.importer.metadata.MeshResourceMetadata;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import javax.annotation.Nullable;
import java.util.*;

import static org.lwjgl.assimp.Assimp.*;

@PBean
public class MeshImporter extends AbstractImporter {
    private static final int FLAGS = Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs | Assimp.aiProcess_GlobalScale | Assimp.aiProcess_FindInstances | Assimp.aiProcess_PreTransformVertices | aiProcess_GenSmoothNormals | aiProcess_DropNormals;

    @PInject
    public TextureImporter textureImporter;

    @Nullable
    private AIScene loadScene(String path) {
        return Assimp.aiImportFile(path, FLAGS);
    }

    @Override
    public List<AbstractImportData> importFile(String path) {
        try {
            Map<Integer, List<AssimpMeshRepresentation>> byPrimitive = new HashMap<>();
            AIScene scene = loadScene(path);
            getLogger().warn("Loading scene from file {}", path);

            if (scene == null) {
                getLogger().error("Failed to load scene at {}", path);
                return Collections.emptyList();
            }
            AIString sceneName = scene.mName();
            SceneImportData sceneImportData = new SceneImportData(FSUtil.getNameFromPath(path));
            var root = new AssimpMeshRepresentation(null);
            root.name = sceneName.dataString();
            MeshImporterUtil.traverseAssimpTree(scene.mRootNode(), root, byPrimitive);
            Map<Integer, AbstractImportData> processed = new HashMap<>();
            Map<Integer, AbstractImportData> processedMaterials = new HashMap<>();
            Map<String, AbstractImportData> textures = new HashMap<>();

            PointerBuffer materialsBuffer = scene.mMaterials();
            traverseTree(root, sceneImportData, processed, scene, root.name, 0, processedMaterials, materialsBuffer, textures);

            Assimp.aiReleaseImport(scene);
            List<AbstractImportData> values = new ArrayList<>(processed.values());
            values.add(sceneImportData);
            values.addAll(processedMaterials.values());
            values.addAll(textures.values());

            getLogger().warn("Meshes {}", processed.keySet());
            getLogger().warn("Materials {}", processedMaterials.keySet());
            getLogger().warn("Textures {}", textures.keySet());

            return values;
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private void traverseTree(AssimpMeshRepresentation root, SceneImportData parent, Map<Integer, AbstractImportData> processed, AIScene scene, String sceneName, int depth, Map<Integer, AbstractImportData> processedMaterials, PointerBuffer materialsBuffer, Map<String, AbstractImportData> textures) {
        AbstractImportData primitive = createPrimitive(root, processed, scene);
        AbstractImportData material = createMaterial(root, sceneName, processedMaterials, materialsBuffer, textures);
        var sceneLocal = new SceneImportData(root.name);
        parent.children.add(sceneLocal);
        sceneLocal.meshResourceId = primitive != null ? primitive.id : null;
        sceneLocal.materialResourceId = material != null ? material.id : null;

        for (var child : root.children) {
            traverseTree(child, sceneLocal, processed, scene, sceneName, depth + 1, processedMaterials, materialsBuffer, textures);
        }
    }

    private AbstractImportData createPrimitive(AssimpMeshRepresentation root, Map<Integer, AbstractImportData> processed, AIScene scene) {
        if (!processed.containsKey(root.mesh) && root.mesh != null) {
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(root.mesh));
            root.material = mesh.mMaterialIndex();
            processed.put(root.mesh, MeshImporterUtil.processPrimitive(mesh));
        }
        return root.mesh != null ? processed.get(root.mesh) : null;
    }

    private AbstractImportData createMaterial(AssimpMeshRepresentation root, String sceneName, Map<Integer, AbstractImportData> processedMaterials, PointerBuffer materialsBuffer, Map<String, AbstractImportData> textures) {
        Integer materialIndex = root.material;
        if (!processedMaterials.containsKey(materialIndex) && materialIndex != null && materialIndex >= 0) {
            AIMaterial aiMaterial = AIMaterial.create(materialsBuffer.get(materialIndex));
            List<MaterialTextureData> texturesToLoad = MeshImporterUtil.processMaterial(aiMaterial, materialIndex, sceneName);

            var material = new MaterialImportData(sceneName + "-material-" + materialIndex);
            for (MaterialTextureData cmd : texturesToLoad) {
                String textureId;
                if (!textures.containsKey(cmd.path())) {
                    AbstractImportData imported = textureImporter.importFile(cmd.path()).getFirst();
                    textures.put(imported.id, imported);
                    textureId = imported.id;
                } else {
                    textureId = textures.get(cmd.path()).id;
                }

                if (cmd.type() == aiTextureType_HEIGHT) {
                    material.heightMap = textureId;
                }
                if (cmd.type() == aiTextureType_NORMALS) {
                    material.normal = textureId;
                }
                if (cmd.type() == aiTextureType_BASE_COLOR) {
                    material.albedo = textureId;
                }
                if (cmd.type() == aiTextureType_METALNESS) {
                    material.metallic = textureId;
                }
                if (cmd.type() == aiTextureType_DIFFUSE_ROUGHNESS) {
                    material.roughness = textureId;
                }
                if (cmd.type() == aiTextureType_AMBIENT_OCCLUSION) {
                    material.ao = textureId;
                }
            }
            processedMaterials.put(materialIndex, material);
        }
        return materialIndex != null ? processedMaterials.get(materialIndex) : null;
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MESH;
    }

    @Override
    public AbstractResourceMetadata persist(AbstractImportData data) {
        var cast = (MeshImportData) data;
        persistInternal(data);
        return new MeshResourceMetadata(data.name, data.id, cast.vertices.length, cast.indices.length, cast.normals != null, cast.uvs != null);
    }
}
