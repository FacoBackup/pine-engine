package com.pine.service.request;

import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.component.MeshComponent;
import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.streaming.scene.SceneStreamData;

public class LoadSceneRequest extends AbstractRequest {
    private final SceneStreamData scene;
    private final Entity root;

    public LoadSceneRequest(Entity root, SceneStreamData scene) {
        this.scene = scene;
        this.root = root;
    }

    @Override
    public Message run(WorldRepository repository, StreamingRepository streamingRepository) {
        traverse(root, scene, repository);
        return new Message("Scene loaded successfully", MessageSeverity.SUCCESS);
    }

    private void traverse(Entity parent, SceneStreamData localScene, WorldRepository repository) {
        Entity newEntity = new Entity();
        newEntity.name = localScene.name;
        if (localScene.meshResourceId != null) {
            MeshComponent meshComponent = new MeshComponent(newEntity);
            repository.registerComponent(meshComponent);
            newEntity.components.put(ComponentType.MESH, meshComponent);
            meshComponent.lod0 = localScene.meshResourceId;
            meshComponent.material = localScene.materialResourceId;
        }
        newEntity.transformation.parent = parent.transformation;
        parent.transformation.children.add(newEntity.transformation);
        for (var childScene : localScene.children) {
            traverse(newEntity, childScene, repository);
        }
    }
}
