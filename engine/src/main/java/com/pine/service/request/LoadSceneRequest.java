package com.pine.service.request;

import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.component.MeshComponent;
import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.importer.data.SceneImportData;

import java.util.LinkedList;

public class LoadSceneRequest extends AbstractRequest {
    private final SceneImportData scene;
    private final Entity root;

    public LoadSceneRequest(Entity root, SceneImportData scene) {
        this.scene = scene;
        this.root = root;
    }

    @Override
    public Message run(WorldRepository repository, StreamingRepository streamingRepository) {
        traverse(root, scene, repository);
        return new Message("Scene loaded successfully", MessageSeverity.SUCCESS);
    }

    private void traverse(Entity parent, SceneImportData localScene, WorldRepository repository) {
        Entity entity = new Entity();
        entity.name = localScene.name;
        if (localScene.meshResourceId != null) {
            MeshComponent meshComponent = new MeshComponent(entity.id());
            repository.registerComponent(meshComponent);
            repository.components.get(ComponentType.MESH).put(entity.id(), meshComponent);
            meshComponent.lod0 = localScene.meshResourceId;
            meshComponent.material = localScene.materialResourceId;
        }

        repository.childParent.put(entity.id(), parent.id());
        repository.parentChildren.putIfAbsent(parent.id(), new LinkedList<>());
        repository.parentChildren.get(parent.id()).add(entity.id());

        for (var childScene : localScene.children) {
            traverse(entity, childScene, repository);
        }
    }
}
