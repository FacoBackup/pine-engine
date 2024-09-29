package com.pine.tasks;

import com.pine.Loggable;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.CullingComponent;
import com.pine.component.InstancedPrimitiveComponent;
import com.pine.component.PrimitiveComponent;
import com.pine.component.TransformationComponent;
import com.pine.repository.CameraRepository;
import com.pine.repository.CorePrimitiveRepository;
import com.pine.repository.CoreSSBORepository;
import com.pine.repository.RenderingRepository;
import com.pine.repository.rendering.PrimitiveRenderRequest;
import com.pine.service.LightService;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.service.resource.primitives.mesh.Primitive;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;


/**
 * Collects all visible renderable elements into a list
 */
@PBean
public class RenderingTask extends AbstractTask implements Loggable {

    public static MeshRenderingMode DEFAULT_RENDERING_MODE = MeshRenderingMode.TRIANGLES;
    private static final MeshRuntimeData DEFAULT_RENDER_REQUEST = new MeshRuntimeData(MeshRenderingMode.TRIANGLES);

    @PInject
    public PrimitiveComponent scenes;

    @PInject
    public InstancedPrimitiveComponent instancedComponents;

    @PInject
    public ResourceService resourceService;

    @PInject
    public CameraRepository camera;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public CoreSSBORepository ssboRepository;

    @PInject
    public TransformationComponent transformationComponent;

    @PInject
    public LightService lightService;

    private List<PrimitiveRenderRequest> temp = new ArrayList<>();
    private final Vector3f distanceAux = new Vector3f();
    private final Vector3f auxCubeMax = new Vector3f();
    private final Vector3f auxCubeMin = new Vector3f();
    private int offset = 0;
    private int requestCount = 0;

    @Override
    protected void tickInternal() {
      try {
          requestCount = 0;
          int instancedOffset = 0;
          offset = 0;
          DEFAULT_RENDER_REQUEST.mode = DEFAULT_RENDERING_MODE;

          temp.clear();
          List<PrimitiveComponent> bag = scenes.getBag();
          for (int i = 0, bagSize = bag.size(); i < bagSize; i++) {
              var scene = bag.get(i);
              PrimitiveRenderRequest request = preparePrimitive(scene);
              if (request != null) {
                  request.renderIndex = i + instancedOffset;
                  instancedOffset += request.transformations.size();
                  temp.add(request);
              }
          }

          List<InstancedPrimitiveComponent> instancedComponentsBag = instancedComponents.getBag();
          for (int i = 0, instancedComponentsBagSize = instancedComponentsBag.size(); i < instancedComponentsBagSize; i++) {
              var scene = instancedComponentsBag.get(i);
              PrimitiveRenderRequest request = prepareInstanced(scene);
              if (request != null) {
                  request.renderIndex = i + instancedOffset;
                  instancedOffset += request.transformations.size();
                  temp.add(request);
              }
          }

          renderingRepository.requestCount = requestCount;


          lightService.packageLights();
          List<PrimitiveRenderRequest> aux = renderingRepository.requests;
          renderingRepository.requests = temp;
          temp = aux;
          renderingRepository.infoUpdated = true;
      }catch (Exception e){
          getLogger().error(e.getMessage(), e);
      }
    }

    private PrimitiveRenderRequest preparePrimitive(PrimitiveComponent scene) {
        if (scene.primitive == null) {
            return null;
        }
        var mesh = scene.primitive.resource = scene.primitive.resource == null ? (Primitive) resourceService.getOrCreateResource(scene.primitive.id) : scene.primitive.resource;
        if (mesh != null) {
            var transform = (TransformationComponent) scene.entity.components.get(TransformationComponent.class.getSimpleName());
            var culling = (CullingComponent) scene.entity.components.get(CullingComponent.class.getSimpleName());
            if (!isCulled(transform.translation, culling.maxDistanceFromCamera, culling.frustumBoxDimensions)) {
                if (transform.renderRequest == null) {
                    transform.renderRequest = new PrimitiveRenderRequest(mesh, DEFAULT_RENDER_REQUEST, (TransformationComponent) scene.entity.components.get(TransformationComponent.class.getSimpleName()));
                }
                transform.renderRequest.primitive = mesh;
                return transform.renderRequest;
            }
        }
        return null;
    }

    private PrimitiveRenderRequest prepareInstanced(InstancedPrimitiveComponent scene) {
        if (scene.primitive == null) {
            return null;
        }

        var mesh = scene.primitive.resource = scene.primitive.resource == null ? (Primitive) resourceService.getOrCreateResource(scene.primitive.id) : scene.primitive.resource;
        if (mesh == null) {
            return null;
        }

        scene.runtimeData = scene.runtimeData == null ? new MeshRuntimeData(DEFAULT_RENDERING_MODE) : scene.runtimeData;

        if (scene.primitives.size() > scene.numberOfInstances) {
            scene.primitives = new ArrayList<>(scene.primitives.subList(0, scene.numberOfInstances));
        } else if (scene.primitives.size() < scene.numberOfInstances) {
            for (int i = scene.primitives.size(); i < scene.numberOfInstances; i++) {
                scene.primitives.add(new TransformationComponent(scene.entity, transformationComponent.bag));
            }
        }


        if (scene.renderRequest == null) {
            scene.renderRequest = new PrimitiveRenderRequest(mesh, scene.runtimeData, null, new ArrayList<>());
        }
        scene.renderRequest.transformations.clear();

        int realNumberOfInstances = 0;
        CullingComponent culling = (CullingComponent) scene.entity.components.get(CullingComponent.class.getSimpleName());
        for (var primitive : scene.primitives) {
            boolean culled = isCulled(primitive.translation, culling.maxDistanceFromCamera, culling.frustumBoxDimensions);
            if (culled) {
                continue;
            }
            realNumberOfInstances++;
            extractTransformations(primitive);
            scene.renderRequest.transformations.add(primitive);
        }
        scene.runtimeData.instanceCount = realNumberOfInstances;

        scene.renderRequest.primitive = mesh;
        return scene.renderRequest;
    }

    private void fillTransformations(Vector3f transformation) {
        ssboRepository.transformationSSBOState.put(offset, transformation.x);
        ssboRepository.transformationSSBOState.put(offset + 1, transformation.y);
        ssboRepository.transformationSSBOState.put(offset + 2, transformation.z);
        offset += 3;
    }

    private void extractTransformations(TransformationComponent st) {
        fillTransformations(st.translation);
        fillTransformations(st.rotation);
        fillTransformations(st.scale);
        st.primitiveIndex = requestCount;
        requestCount++;
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
