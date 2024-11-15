package com.pine.service.request;

import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.component.MeshComponent;
import com.pine.service.importer.data.SceneImportData;

import java.util.Collections;
import java.util.List;

public class LoadSceneRequest extends AbstractRequest {
    private final SceneImportData scene;
    private final Entity root;

    public LoadSceneRequest(Entity root, SceneImportData scene) {
        this.scene = scene;
        this.root = root;
    }

    @Override
    public void run() {
        getLogger().warn("Loading scene {}", scene.id);
        traverse(root, scene);
    }

    private void traverse(Entity parent, SceneImportData localScene) {
        var add = new AddEntityRequest(localScene.meshResourceId != null ? List.of(ComponentType.MESH) : Collections.emptyList());
        add.setup(world, streamingRepository, worldService);
        add.run();

        Entity entity = add.getResponse();
        entity.name = localScene.name;
        if (localScene.meshResourceId != null) {
            var comp = (MeshComponent) world.bagMeshComponent.get(entity.id());
            comp.lod0 = localScene.meshResourceId;
            comp.material = localScene.materialResourceId;
        }

        var hierarchy = new HierarchyRequest(parent, entity);
        hierarchy.setup(world, streamingRepository, worldService);
        hierarchy.run();

        for (var childScene : localScene.children) {
            traverse(entity, childScene);
        }
    }
}
