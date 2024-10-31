package com.pine.service.rendering;

import com.pine.EngineUtils;
import com.pine.component.MeshComponent;
import com.pine.component.TransformationComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.core.CoreSSBORepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.tasks.AbstractTask;
import org.joml.Matrix4f;
import org.joml.Vector3f;


@PBean
public class TransformationService extends AbstractTask {
    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public CoreSSBORepository ssboRepository;

    @PInject
    public WorldRepository worldRepository;

    @PInject
    public LightService lightService;

    private final Vector3f distanceAux = new Vector3f();
    private final Vector3f auxCubeMax = new Vector3f();
    private final Vector3f auxCubeMin = new Vector3f();
    private final Matrix4f auxMat4 = new Matrix4f();
    private final Matrix4f auxMat42 = new Matrix4f();
    private boolean isLightModified = false;

    @Override
    protected void tickInternal() {
        startTracking();
        try {
            isLightModified = false;
            traverse(WorldRepository.ROOT_ID, false);
            if (isLightModified) {
                lightService.packageLights();
            }
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
        endTracking();
    }

    public void traverse(String root, boolean parentHasChanged) {
        TransformationComponent st = worldRepository.bagTransformationComponent.get(root);
        if (st != null && (st.isNotFrozen() || parentHasChanged)) {
            TransformationComponent parentTransform = findParent(st.getEntityId());
            transform(st, parentTransform);
            st.freezeVersion();
            parentHasChanged = true;
        }

        var mesh = (MeshComponent) worldRepository.bagMeshComponent.get(root);
        if (mesh != null) {
            if (mesh.isInstancedRendering) {
                mesh.instances.forEach(t -> {
                    transform(t, st);
                });
            }
        }

        var children = worldRepository.parentChildren.get(root);
        if (children != null) {
            for (var child : children) {
                traverse(child, parentHasChanged);
            }
        }
    }


    private void transform(TransformationComponent st, TransformationComponent parentTransform) {
        if (parentTransform != null) {
            auxMat4.set(parentTransform.globalMatrix);
        } else {
            auxMat4.identity();
        }

        auxMat42.identity();
        auxMat42
                .translate(st.translation)
                .rotate(st.rotation)
                .scale(st.scale);

        auxMat4.mul(auxMat42);
        st.globalMatrix.set(auxMat4);
        st.freezeVersion();

        if (!isLightModified && (
                worldRepository.bagDirectionalLightComponent.containsKey(st.getEntityId()) ||
                        worldRepository.bagPointLightComponent.containsKey(st.getEntityId()) ||
                        worldRepository.bagSphereLightComponent.containsKey(st.getEntityId()) ||
                        worldRepository.bagSpotLightComponent.containsKey(st.getEntityId()))) {
            isLightModified = true;
        }
    }

    private TransformationComponent findParent(String id) {
        while (id != null && !id.equals(WorldRepository.ROOT_ID)) {
            id = worldRepository.childParent.get(id);
            var t = worldRepository.bagTransformationComponent.get(id);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    public void extractTransformations(TransformationComponent st) {
        EngineUtils.copyWithOffset(ssboRepository.transformationSSBOState, st.globalMatrix, renderingRepository.offset);
        renderingRepository.offset += 16;
    }

    public float getDistanceFromCamera(Vector3f translation) {
        distanceAux.set(cameraRepository.currentCamera.position);
        return distanceAux.sub(translation).length();
    }

    public boolean isCulled(Vector3f translation, float maxDistanceFromCamera, Vector3f boundingBoxSize) {
        if (getDistanceFromCamera(translation) > maxDistanceFromCamera) {
            return true;
        }

        auxCubeMin.x = translation.x - boundingBoxSize.x;
        auxCubeMin.y = translation.y - boundingBoxSize.y;
        auxCubeMin.z = translation.x - boundingBoxSize.z;

        auxCubeMax.x = translation.x + boundingBoxSize.x;
        auxCubeMax.y = translation.y + boundingBoxSize.y;
        auxCubeMax.z = translation.x + boundingBoxSize.z;

        // TODO - FIX
        return !cameraRepository.frustum.isCubeInFrustum(auxCubeMin, auxCubeMax);
    }

    @Override
    public String getTitle() {
        return "Transformation";
    }
}
