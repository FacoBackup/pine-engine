package com.pine.tasks;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.*;
import com.pine.component.rendering.SimpleTransformation;
import com.pine.repository.CameraRepository;
import com.pine.repository.CoreSSBORepository;
import com.pine.repository.RenderingRepository;
import com.pine.repository.rendering.PrimitiveRenderRequest;
import com.pine.service.resource.MeshService;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.service.resource.primitives.mesh.Primitive;
import com.pine.service.world.WorldService;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.pine.repository.CoreSSBORepository.INFO_PER_LIGHT;


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

    @PInject
    public CoreSSBORepository ssboRepository;

    @PInject
    public LightComponent lights;

    private List<PrimitiveRenderRequest> temp = new ArrayList<>();
    private final Vector3f distanceAux = new Vector3f();
    private final Vector3f auxCubeMax = new Vector3f();
    private final Vector3f auxCubeMin = new Vector3f();
    private int offset = 0;

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

        packageLights();

        collectTransformations();
        List<PrimitiveRenderRequest> aux = renderingRepository.requests;
        renderingRepository.requests = temp;
        temp = aux;
        renderingRepository.infoUpdated = true;
    }

    private void packageLights() {
        Vector<LightComponent> bag = lights.getBag();
        int offset = 0;
        for (int i = 0; i < bag.size(); i++) {
            var light = bag.get(i);
            TransformationComponent transform = worldService.getTransformationComponentUnchecked(light.getEntityId());
            ssboRepository.lightSSBOState.put(i + offset, light.screenSpaceShadows ? 1 : 0);
            ssboRepository.lightSSBOState.put(i + offset + 1, light.shadowMap ? 1 : 0);
            ssboRepository.lightSSBOState.put(i + offset + 2, light.shadowBias);
            ssboRepository.lightSSBOState.put(i + offset + 3, light.shadowSamples);
            ssboRepository.lightSSBOState.put(i + offset + 4, light.zNear);
            ssboRepository.lightSSBOState.put(i + offset + 5, light.zFar);
            ssboRepository.lightSSBOState.put(i + offset + 6, light.cutoff);
            ssboRepository.lightSSBOState.put(i + offset + 7, light.shadowAttenuationMinDistance);
            ssboRepository.lightSSBOState.put(i + offset + 8, light.smoothing);
            ssboRepository.lightSSBOState.put(i + offset + 9, light.radius);
            ssboRepository.lightSSBOState.put(i + offset + 10, light.size);
            ssboRepository.lightSSBOState.put(i + offset + 11, light.areaRadius);
            ssboRepository.lightSSBOState.put(i + offset + 12, light.planeAreaWidth);
            ssboRepository.lightSSBOState.put(i + offset + 13, light.planeAreaHeight);
            ssboRepository.lightSSBOState.put(i + offset + 14, light.intensity);
            ssboRepository.lightSSBOState.put(i + offset + 15, light.type.getTypeId());
            ssboRepository.lightSSBOState.put(i + offset + 16, light.color.x);
            ssboRepository.lightSSBOState.put(i + offset + 17, light.color.y);
            ssboRepository.lightSSBOState.put(i + offset + 18, light.color.z);

            ssboRepository.lightSSBOState.put(i + offset + 19, transform.translation.x);
            ssboRepository.lightSSBOState.put(i + offset + 20, transform.translation.y);
            ssboRepository.lightSSBOState.put(i + offset + 21, transform.translation.z);

            ssboRepository.lightSSBOState.put(i + offset + 22, transform.rotation.x);
            ssboRepository.lightSSBOState.put(i + offset + 23, transform.rotation.y);
            ssboRepository.lightSSBOState.put(i + offset + 24, transform.rotation.z);

            ssboRepository.lightSSBOState.put(i + offset + 25, light.atlasFace.x);
            ssboRepository.lightSSBOState.put(i + offset + 26, light.atlasFace.y);

            ssboRepository.lightSSBOState.put(i + offset + 27, light.attenuation.x);
            ssboRepository.lightSSBOState.put(i + offset + 28, light.attenuation.y);

            offset += INFO_PER_LIGHT;
        }
        renderingRepository.lightCount = bag.size();
    }

    private void collectTransformations() {
        offset = 0;
        int requestCount = 0;
        for (PrimitiveRenderRequest request : temp) {
            if (request.transformations.isEmpty()) {
                fillTransformations(request.transformation.translation);
                fillTransformations(request.transformation.rotation);
                fillTransformations(request.transformation.scale);
                request.transformation.renderIndex = requestCount;
                requestCount++;
            } else {
                for (SimpleTransformation st : request.transformations) {
                    fillTransformations(st.translation);
                    fillTransformations(st.rotation);
                    fillTransformations(st.scale);
                    st.renderIndex = requestCount;
                    requestCount++;
                }
            }
        }
        renderingRepository.requestCount = requestCount;
    }

    private void fillTransformations(Vector3f transformation) {
        ssboRepository.transformationSSBOState.put(offset, transformation.x);
        ssboRepository.transformationSSBOState.put(offset + 1, transformation.y);
        ssboRepository.transformationSSBOState.put(offset + 2, transformation.z);
        offset += 3;
    }

    private void prepareComposite(SceneComponent scene) {
        if (scene.requests.size() != scene.compositeScene.primitives.size()) {
            scene.requests.clear();
            for (var primitive : scene.compositeScene.primitives) {
                if (primitive.primitive == null) {
                    continue;
                }
                primitive.transformation.parentTransformationId = scene.getEntityId();
                var mesh = primitive.primitive.resource = primitive.primitive.resource == null ? (Primitive) resourceService.getOrCreateResource(primitive.primitive.id) : primitive.primitive.resource;
                if (mesh != null) {
                    scene.requests.add(new PrimitiveRenderRequest(mesh, DEFAULT_RENDER_REQUEST, primitive.transformation));
                }
            }
        }

        CullingComponent culling = worldService.getCullingComponentUnchecked(scene.getEntityId());
        for (var r : scene.requests) {
            boolean culled = isCulled(r.transformation.translation, culling.maxDistanceFromCamera, culling.frustumBoxDimensions);
            if (culled) {
                continue;
            }
            temp.add(r);
        }
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
                scene.request = new PrimitiveRenderRequest(scene.meshInstance, DEFAULT_RENDER_REQUEST, transformation.toSimpleTransformation());
            }
        }
        temp.add(scene.request);
    }

    private void prepareInstanced(InstancedSceneComponent scene) {
        if (scene.primitive == null) {
            return;
        }

        if (scene.runtimeData == null) {
            scene.runtimeData = new MeshRuntimeData(DEFAULT_RENDERING_MODE);
        }

        var mesh = scene.primitive.resource = scene.primitive.resource == null ? (Primitive) resourceService.getOrCreateResource(scene.primitive.id) : scene.primitive.resource;

        if (scene.compositeScene.primitives.size() > scene.numberOfInstances) {
            scene.compositeScene.primitives = scene.compositeScene.primitives.subList(0, scene.numberOfInstances);
        } else if (scene.compositeScene.primitives.size() < scene.numberOfInstances) {
            for (int i = 0; i < scene.numberOfInstances; i++) {
                scene.compositeScene.addPrimitive();
            }
        }

        if (mesh != null) {
            int realNumberOfInstances = 0;
            CullingComponent culling = worldService.getCullingComponentUnchecked(scene.getEntityId());


            List<SimpleTransformation> transformations;
            PrimitiveRenderRequest composite;
            if (scene.request == null) {
                transformations = new ArrayList<>();
                composite = new PrimitiveRenderRequest(mesh, scene.runtimeData, transformations, scene.getEntityId());
            } else {
                composite = scene.request;
                transformations = scene.request.transformations;
                transformations.clear();
            }

            for (var primitive : scene.compositeScene.primitives) {
                boolean culled = isCulled(primitive.transformation.translation, culling.maxDistanceFromCamera, culling.frustumBoxDimensions);
                if (culled) {
                    continue;
                }
                realNumberOfInstances++;
                primitive.transformation.parentTransformationId = scene.getEntityId();
                transformations.add(primitive.transformation);
            }
            scene.request = composite;

            scene.request.primitive = mesh;
            scene.runtimeData.instanceCount = realNumberOfInstances;
            temp.add(scene.request);
        }
    }

    private boolean isCulled(int entityId) {
        TransformationComponent t = worldService.getTransformationComponentUnchecked(entityId);
        CullingComponent c = worldService.getCullingComponentUnchecked(entityId);
        return isCulled(t.translation, c.maxDistanceFromCamera, c.frustumBoxDimensions);
    }

    private boolean isCulled(Vector3f translation, float maxDistanceFromCamera, Vector3f frustumBoxDimensions) {
        distanceAux.set(camera.currentCamera.position);
        if (Math.abs(distanceAux.sub(translation).length()) > maxDistanceFromCamera) {
            return true;
        }

        auxCubeMin.x = translation.x - frustumBoxDimensions.x;
        auxCubeMin.y = translation.y - frustumBoxDimensions.y;
        auxCubeMin.z = translation.x - frustumBoxDimensions.z;

        auxCubeMax.x = translation.x + frustumBoxDimensions.x;
        auxCubeMax.y = translation.y + frustumBoxDimensions.y;
        auxCubeMax.z = translation.x + frustumBoxDimensions.z;

        return !camera.frustum.isCubeInFrustum(auxCubeMin, auxCubeMax);
    }
}
