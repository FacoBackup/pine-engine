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
        add.setup(repository, streamingRepository);
        add.run();


        Entity entity = add.getResponse();
        entity.name = localScene.name;
        if (localScene.meshResourceId != null) {
            var comp = (MeshComponent) repository.components.get(ComponentType.MESH).get(entity.id());
            comp.lod0 = localScene.meshResourceId;
            comp.material = localScene.materialResourceId;
        }

        var hierarchy = new HierarchyRequest(parent, entity);
        hierarchy.setup(repository, streamingRepository);
        hierarchy.run();

        for (var childScene : localScene.children) {
            traverse(entity, childScene);
        }
    }
}
