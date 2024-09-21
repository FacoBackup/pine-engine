package com.pine.tasks;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.*;
import com.pine.component.rendering.SimpleTransformation;
import com.pine.repository.CameraRepository;
import com.pine.repository.RenderingRepository;
import com.pine.repository.rendering.CompositeDrawDTO;
import com.pine.repository.rendering.RuntimeDrawDTO;
import com.pine.service.resource.MeshService;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.primitives.mesh.MeshPrimitiveResource;
import com.pine.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.service.world.WorldService;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects all visible renderable elements into a list
 */
@PBean
public class RenderingTask extends AbstractTask {

    public static MeshRenderingMode DEFAULT_RENDERING_MODE = MeshRenderingMode.TRIANGLES;
    private static final MeshRuntimeData DEFAULT_RENDER_REQUEST = new MeshRuntimeData(MeshRenderingMode.TRIANGLES);

    @PInject
    public SceneComponent scenes;

    @PInject
    public TerrainComponent terrains;

    @PInject
    public InstancedSceneComponent instancedComponents;

    @PInject
    public ResourceService resourceService;

    @PInject
    public WorldService worldService;

    @PInject
    public CameraRepository camera;

    @PInject
    public MeshService meshService;

    @PInject
    public RenderingRepository renderingRepository;

    private List<RuntimeDrawDTO> temp = new ArrayList<>();
    private final Vector3f distanceAux = new Vector3f();
    private final Vector3f auxCubeMax = new Vector3f();
    private final Vector3f auxCubeMin = new Vector3f();

    @Override
    protected void tickInternal() {
        DEFAULT_RENDER_REQUEST.mode = DEFAULT_RENDERING_MODE;

        temp.clear();
        for (var scene : scenes.getBag()) {
            prepareComposite(scene);
        }

        for (var scene : instancedComponents.getBag()) {
            prepareInstanced(scene);
        }

        for (var scene : terrains.getBag()) {
            prepareTerrain(scene);
        }

        List<RuntimeDrawDTO> aux = renderingRepository.requests;
        renderingRepository.requests = temp;
        temp = aux;
    }

    private void prepareComposite(SceneComponent scene) {
        boolean culled = isCulled(scene.getEntityId());
        if (culled) {
            return;
        }

        if (scene.requests.size() != scene.compositeScene.primitives.size()) {
            scene.requests.clear();
            for (var primitive : scene.compositeScene.primitives) {
                if (primitive.primitive == null) {
                    continue;
                }
                primitive.transformation.parentTransformationId = scene.getEntityId();
                var mesh = primitive.primitive.resource = primitive.primitive.resource == null ? (MeshPrimitiveResource) resourceService.getOrCreateResource(primitive.primitive.id) : primitive.primitive.resource;
                if (mesh != null) {
                    scene.requests.add(new RuntimeDrawDTO(mesh, DEFAULT_RENDER_REQUEST, primitive.transformation));
                }
            }
        }
        temp.addAll(scene.requests);
    }

    private void prepareTerrain(TerrainComponent scene) {
        if (scene.heightMapTexture == null) {
            return;
        }

        boolean culled = isCulled(scene.getEntityId());
        if (culled) {
            return;
        }

        if (scene.request == null) {
            TransformationComponent transformation = worldService.getTransformationComponentUnchecked(scene.getEntityId());
            if (scene.meshInstance == null) {
                scene.meshInstance = meshService.createTerrain(scene.heightMapTexture.id);
            }
            if (scene.meshInstance != null) {
                scene.request = new RuntimeDrawDTO(scene.meshInstance, DEFAULT_RENDER_REQUEST, transformation.toSimpleTransformation());
            }
        }
        temp.add(scene.request);
    }

    private void prepareInstanced(InstancedSceneComponent scene) {
        if (scene.primitive == null) {
            return;
        }

        boolean culled = isCulled(scene.getEntityId());
        if (culled) {
            return;
        }

        if (scene.runtimeData == null) {
            scene.runtimeData = new MeshRuntimeData(DEFAULT_RENDERING_MODE);
        }
        scene.runtimeData.instanceCount = scene.numberOfInstances;

        var mesh = scene.primitive.resource = scene.primitive.resource == null ? (MeshPrimitiveResource) resourceService.getOrCreateResource(scene.primitive.id) : scene.primitive.resource;
        if (scene.request == null && mesh != null) {
            List<SimpleTransformation> transformations = new ArrayList<>();
            CompositeDrawDTO composite = new CompositeDrawDTO(mesh, scene.runtimeData, transformations);
            for (var primitive : scene.compositeScene.primitives) {
                primitive.transformation.parentTransformationId = scene.getEntityId();
                transformations.add(primitive.transformation);
            }
            scene.request = composite;
        }
        if (scene.request != null) {
            scene.request.primitive = mesh;
            temp.add(scene.request);
        }
    }

    private boolean isCulled(int entityId) {
        TransformationComponent t = worldService.getTransformationComponentUnchecked(entityId);
        CullingComponent c = worldService.getCullingComponentUnchecked(entityId);
        distanceAux.set(camera.currentCamera.position);
        if (Math.abs(distanceAux.sub(t.translation).length()) > c.maxDistanceFromCamera) {
            return true;
        }

        auxCubeMin.x = t.translation.x - c.frustumBoxDimensions.x;
        auxCubeMin.y = t.translation.y - c.frustumBoxDimensions.y;
        auxCubeMin.z = t.translation.x - c.frustumBoxDimensions.z;

        auxCubeMax.x = t.translation.x + c.frustumBoxDimensions.x;
        auxCubeMax.y = t.translation.y + c.frustumBoxDimensions.y;
        auxCubeMax.z = t.translation.x + c.frustumBoxDimensions.z;

        return !camera.frustum.isCubeInFrustum(auxCubeMin, auxCubeMax);
    }
}
