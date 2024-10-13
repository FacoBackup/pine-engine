package com.pine.service.importer.impl.mesh;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.material.MaterialStreamData;
import com.pine.service.streaming.material.MaterialStreamableResource;
import com.pine.service.streaming.mesh.MeshStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.AbstractImporter;
import com.pine.service.streaming.mesh.MeshStreamData;
import com.pine.service.streaming.scene.SceneStreamData;
import com.pine.service.streaming.scene.SceneStreamableResource;
import com.pine.service.streaming.texture.TextureStreamableResource;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import javax.annotation.Nullable;
import java.util.*;

import static org.lwjgl.assimp.Assimp.*;

@PBean
public class MeshImporter extends AbstractImporter {
    private static final int FLAGS = Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs | Assimp.aiProcess_GlobalScale | Assimp.aiProcess_FindInstances | Assimp.aiProcess_PreTransformVertices | aiProcess_GenSmoothNormals | aiProcess_DropNormals;

    @PInject
    public StreamingService streamingService;

    @Nullable
    private AIScene loadScene(String path) {
        return Assimp.aiImportFile(path, FLAGS);
    }

    @Override
    public List<AbstractStreamableResource<?>> load(String path) {
        try {
            Map<Integer, List<AssimpMeshRepresentation>> byPrimitive = new HashMap<>();
            AIScene scene = loadScene(path);
            getLogger().warn("Loading scene from file {}", path);

            if (scene == null) {
                getLogger().error("Failed to load scene at {}", path);
                return Collections.emptyList();
            }
            AIString sceneName = scene.mName();
            SceneStreamData sceneStreamData = new SceneStreamData(FSUtil.getNameFromPath(path));
            var root = new AssimpMeshRepresentation(null);
            root.name = sceneName.dataString();
            MeshImporterUtil.traverseAssimpTree(scene.mRootNode(), root, byPrimitive);
            Map<Integer, MeshStreamableResource> processed = new HashMap<>();
            Map<Integer, MaterialStreamableResource> processedMaterials = new HashMap<>();
            Map<String, TextureStreamableResource> textures = new HashMap<>();

            PointerBuffer materialsBuffer = scene.mMaterials();
            traverseTree(root, sceneStreamData, processed, scene, root.name, 0, processedMaterials, materialsBuffer, textures);

            SceneStreamableResource sceneStreamable = streamingService.addNew(SceneStreamableResource.class, sceneStreamData.name);
            persist(sceneStreamable, sceneStreamData);

            Assimp.aiReleaseImport(scene);
            List<AbstractStreamableResource<?>> values = new ArrayList<>(processed.values());
            values.add(sceneStreamable);
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

    private void traverseTree(AssimpMeshRepresentation root, SceneStreamData parent, Map<Integer, MeshStreamableResource> processed, AIScene scene, String sceneName, int depth, Map<Integer, MaterialStreamableResource> processedMaterials, PointerBuffer materialsBuffer, Map<String, TextureStreamableResource> textures) {
        MeshStreamableResource primitive = createPrimitive(root, processed, scene);
        MaterialStreamableResource material = createMaterial(root, sceneName, processedMaterials, materialsBuffer, textures);
        var sceneLocal = new SceneStreamData(root.name);
        parent.children.add(sceneLocal);
        sceneLocal.meshResourceId = primitive != null ? primitive.id : null;
        sceneLocal.materialResourceId = material != null ? material.id : null;

        for (var child : root.children) {
            traverseTree(child, sceneLocal, processed, scene, sceneName, depth + 1, processedMaterials, materialsBuffer, textures);
        }
    }

    @Nullable
    private MeshStreamableResource createPrimitive(AssimpMeshRepresentation root, Map<Integer, MeshStreamableResource> processed, AIScene scene) {
        MeshStreamableResource primitive = processed.get(root.mesh);
        if (primitive == null && root.mesh != null) {
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(root.mesh));
            AIString name = mesh.mName();
            CoreMeshData coreMeshData = MeshImporterUtil.processPrimitive(mesh);
            root.material = mesh.mMaterialIndex();
            primitive = streamingService.addNew(MeshStreamableResource.class, name.dataString());
            if (primitive != null) {
                MeshStreamData data = new MeshStreamData(coreMeshData.vertices(), coreMeshData.indices(), coreMeshData.normals(), coreMeshData.uvs());
                primitive.size = persist(primitive, data);
            }
            processed.put(root.mesh, primitive);
        }
        return primitive;
    }

    private MaterialStreamableResource createMaterial(AssimpMeshRepresentation root, String sceneName, Map<Integer, MaterialStreamableResource> processedMaterials, PointerBuffer materialsBuffer, Map<String, TextureStreamableResource> textures) {
        Integer materialIndex = root.material;
        MaterialStreamableResource material = processedMaterials.get(materialIndex);
        if (material == null && materialIndex != null && materialIndex >= 0) {
            AIMaterial aiMaterial = AIMaterial.create(materialsBuffer.get(materialIndex));
            List<MaterialTextureData> texturesToLoad = MeshImporterUtil.processMaterial(aiMaterial, materialIndex, sceneName);

            material = streamingService.addNew(MaterialStreamableResource.class, sceneName + "-material-" + materialIndex);
            if (material != null) {
                MaterialStreamData data = new MaterialStreamData();
                for (MaterialTextureData cmd : texturesToLoad) {
                    TextureStreamableResource texture;
                    if (!textures.containsKey(cmd.path())) {
                        textures.put(cmd.path(), texture = streamingService.addNew(TextureStreamableResource.class, cmd.name()));
                        if (texture != null) {
                            texture.size = persist(texture, cmd.path());
                            if (texture.size < 0) {
                                streamingService.repository.streamableResources.remove(texture);
                                textures.remove(cmd.path());
                                continue;
                            }
                        }
                    } else {
                        texture = textures.get(cmd.path());
                    }

                    if (texture != null && texture.size > 0) {
                        if (cmd.type() == aiTextureType_HEIGHT) {
                            material.heightMap = texture;
                        }
                        if (cmd.type() == aiTextureType_NORMALS) {
                            material.normal = texture;
                        }
                        if (cmd.type() == aiTextureType_BASE_COLOR) {
                            material.albedo = texture;
                        }
                        if (cmd.type() == aiTextureType_METALNESS) {
                            material.metallic = texture;
                        }
                        if (cmd.type() == aiTextureType_DIFFUSE_ROUGHNESS) {
                            material.roughness = texture;
                        }
                        if (cmd.type() == aiTextureType_AMBIENT_OCCLUSION) {
                            material.ao = texture;
                        }
                    }
                }
                material.size = persist(material, data);
            }
            processedMaterials.put(materialIndex, material);
            return material;
        }
        return null;
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MESH;
    }
}
