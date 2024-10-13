package com.pine.service.importer.impl.mesh;

import com.pine.FSUtil;
import com.pine.component.Entity;
import com.pine.component.MeshComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.mesh.MeshStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.AbstractImporter;
import com.pine.service.rendering.RequestProcessingService;
import com.pine.service.request.AddComponentRequest;
import com.pine.service.request.AddEntityRequest;
import com.pine.service.request.HierarchyRequest;
import com.pine.service.resource.ResourceService;
import com.pine.service.streaming.mesh.MeshStreamData;
import com.pine.service.streaming.scene.SceneStreamData;
import com.pine.service.streaming.scene.SceneStreamableResource;
import org.lwjgl.assimp.*;

import javax.annotation.Nullable;
import java.util.*;

@PBean
public class MeshImporter extends AbstractImporter {
    private static final int FLAGS = Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs | Assimp.aiProcess_GlobalScale | Assimp.aiProcess_FindInstances | Assimp.aiProcess_PreTransformVertices | Assimp.aiProcess_GenNormals;

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
            SceneStreamData sceneStreamData = new SceneStreamData(FSUtil.getNameFromPath(path));
            var root = new AssimpMeshRepresentation(null);
            root.name = scene.mName().dataString();
            MeshImporterUtil.traverseAssimpTree(scene.mRootNode(), root, byPrimitive);
            Map<Integer, MeshStreamableResource> processed = new HashMap<>();

            traverseTree(root, sceneStreamData, processed, scene, 0);

            SceneStreamableResource sceneStreamable = streamingService.addNew(SceneStreamableResource.class, sceneStreamData.name);
            persist(sceneStreamable, sceneStreamData);

            Assimp.aiReleaseImport(scene);
            List<AbstractStreamableResource<?>> values = new ArrayList<>(processed.values());
            values.add(sceneStreamable);
            return values;
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private void traverseTree(AssimpMeshRepresentation root, SceneStreamData parent, Map<Integer, MeshStreamableResource> processed, AIScene scene, int depth) {
        MeshStreamableResource primitive = processed.get(root.primitive);
        if (primitive == null && root.primitive != null) {
            primitive = createPrimitive(root, scene);
            processed.put(root.primitive, primitive);
        }

        var sceneLocal = new SceneStreamData(root.name);
        parent.children.add(sceneLocal);
        sceneLocal.meshResourceId = primitive != null ? primitive.id : null;

        for (var child : root.children) {
            traverseTree(child, sceneLocal, processed, scene, depth + 1);
        }
    }

    private MeshStreamableResource createPrimitive(AssimpMeshRepresentation root, AIScene scene) {
        AIMesh mesh = AIMesh.create(scene.mMeshes().get(root.primitive));
        CoreMeshData coreMeshData = MeshImporterUtil.processPrimitive(mesh);
        MeshStreamableResource primitive = streamingService.addNew(MeshStreamableResource.class, mesh.mName().dataString());
        if (primitive != null) {
            MeshStreamData data = new MeshStreamData(coreMeshData.vertices(), coreMeshData.indices(), coreMeshData.normals(), coreMeshData.uvs());
            primitive.size = persist(primitive, data);
        }
        return primitive;
    }


    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MESH;
    }
}
