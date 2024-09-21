package com.pine.tasks;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.*;
import com.pine.component.rendering.SimpleTransformation;
import com.pine.repository.CameraRepository;
import com.pine.repository.CoreResourceRepository;
import com.pine.repository.RenderingRepository;
import com.pine.repository.rendering.CompositeDrawDTO;
import com.pine.repository.rendering.RuntimeDrawDTO;
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
    public WorldService worldService;

    @PInject
    public CoreResourceRepository coreResourceRepository;

    @PInject
    public CameraRepository camera;

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
            boolean culled = isCulled(scene.getEntityId());
            if (culled) {
                continue;
            }

            if (scene.requests.size() != scene.compositeScene.primitives.size()) {
                scene.requests.clear();
                for (var primitive : scene.compositeScene.primitives) {
                    primitive.transformation.parentTransformationId = scene.getEntityId();
                    scene.requests.add(new RuntimeDrawDTO(primitive.primitive, DEFAULT_RENDER_REQUEST, primitive.transformation));
                }
            }
            temp.addAll(scene.requests);
        }

        for (var scene : instancedComponents.getBag()) {
            boolean culled = isCulled(scene.getEntityId());
            if (culled) {
                continue;
            }

            if (scene.runtimeData == null) {
                scene.runtimeData = new MeshRuntimeData(DEFAULT_RENDERING_MODE);
            }
            scene.runtimeData.instanceCount = scene.numberOfInstances;

            if (scene.request == null) {
                List<SimpleTransformation> transformations = new ArrayList<>();
                CompositeDrawDTO composite = new CompositeDrawDTO(scene.primitive, scene.runtimeData, transformations);
                for (var primitive : scene.compositeScene.primitives) {
                    primitive.transformation.parentTransformationId = scene.getEntityId();
                    transformations.add(primitive.transformation);
                }
                scene.request = composite;
            }
            temp.add(scene.request);
        }

        for (var scene : terrains.getBag()) {
            boolean culled = isCulled(scene.getEntityId());
            if (culled) {
                continue;
            }

            if (scene.request == null) {
                TransformationComponent transformation = worldService.getTransformationComponentUnchecked(scene.getEntityId());
                scene.request = new RuntimeDrawDTO(coreResourceRepository.cubeMesh, DEFAULT_RENDER_REQUEST, transformation.toSimpleTransformation());
            }
            temp.add(scene.request);
        }

        List<RuntimeDrawDTO> aux = renderingRepository.requests;
        renderingRepository.requests = temp;
        temp = aux;
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
