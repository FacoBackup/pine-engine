package com.pine.service.rendering;

import com.pine.EngineUtils;
import com.pine.component.ComponentType;
import com.pine.component.MeshComponent;
import com.pine.component.TransformationComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.core.CoreSSBORepository;
import com.pine.repository.rendering.RenderingRepository;
import org.joml.Matrix4f;
import org.joml.Vector3f;


@PBean
public class TransformationService {
    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public CoreSSBORepository ssboRepository;

    @PInject
    public WorldRepository worldRepository;

    private final Vector3f distanceAux = new Vector3f();
    private final Vector3f auxCubeMax = new Vector3f();
    private final Vector3f auxCubeMin = new Vector3f();
    private final Matrix4f auxMat4 = new Matrix4f();
    private final Matrix4f auxMat42 = new Matrix4f();

    public void updateHierarchy(TransformationComponent st) {
        if (st.isInstanced) {
            transform(st, worldRepository.getTransformationComponent(st.entity.id));
            return;
        }
        TransformationComponent parentTransform = findParent(st.entity.id());
        transform(st, parentTransform);

        var children = worldRepository.parentChildren.get(st.entity.id());
        if (children != null) {
            for (String child : children) {
                var comp = worldRepository.getTransformationComponent(child);
                if (comp != null) {
                    updateHierarchy(comp);
                }
            }
        }
        var meshC = (MeshComponent) st.entity.components.get(ComponentType.MESH);
        if (meshC != null && meshC.isInstancedRendering){
            for(var t : meshC.instances){
                updateHierarchy(t);
            }
        }
    }

    public void transform(TransformationComponent st, TransformationComponent parentTransform) {
        if (parentTransform != null) {
            auxMat4.set(parentTransform.globalMatrix);
            transformInternal(st);
        } else if (st.isNotFrozen()) {
            auxMat4.identity();
            transformInternal(st);
        }
    }

    private void transformInternal(TransformationComponent st) {
        auxMat42.identity();
        auxMat42
                .translate(st.translation)
                .rotate(st.rotation)
                .scale(st.scale);

        auxMat4.mul(auxMat42);
        st.globalMatrix.set(auxMat4);
        for (var comp : st.entity.components.values()) {
            if (comp.getType() != ComponentType.TRANSFORMATION && comp.getType() != ComponentType.MESH) {
                comp.registerChange();
            }
        }
    }

    private TransformationComponent findParent(String id) {
        String current = id;
        while (current != null && !current.equals(worldRepository.rootEntity.id())) {
            current = worldRepository.childParent.get(current);
            var t = worldRepository.getTransformationComponent(current);
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

        return !cameraRepository.frustum.isCubeInFrustum(auxCubeMin, auxCubeMax);
    }
}
