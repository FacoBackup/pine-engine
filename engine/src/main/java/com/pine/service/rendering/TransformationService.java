package com.pine.service.rendering;

import com.pine.EngineUtils;
import com.pine.component.Transformation;
import com.pine.component.light.AbstractLightComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.core.CoreSSBORepository;
import com.pine.repository.rendering.RenderingRepository;
import org.joml.Matrix4f;
import org.joml.Vector3f;


@PBean
public class TransformationService {
    private static final float TO_RAD = (float) (Math.PI / 180);
    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public CoreSSBORepository ssboRepository;

    private final Vector3f distanceAux = new Vector3f();
    private final Vector3f auxCubeMax = new Vector3f();
    private final Vector3f auxCubeMin = new Vector3f();
    private final Matrix4f auxMat4 = new Matrix4f();

    public void updateMatrix(Transformation st) {
        updateMatrix(st, st.parent);
    }

    public void updateMatrix(Transformation st, Transformation parentTransform) {
        if (parentTransform != null) {
            auxMat4.set(parentTransform.globalMatrix);
            if (parentTransform.getChangeId() != st.parentChangeId || st.isNotFrozen()) {
                st.parentChangeId = parentTransform.getChangeId();
                transform(st);
            }
        } else if (st.isNotFrozen()) {
            auxMat4.identity();
            transform(st);
        }
    }

    public void extractTransformations(Transformation st) {
        EngineUtils.copyWithOffset(ssboRepository.transformationSSBOState, st.globalMatrix, renderingRepository.offset);
        renderingRepository.offset += 16;
    }

    private void transform(Transformation st) {
        st.localMatrix.identity();
        st.localMatrix
                .translate(st.translation)
                .rotate(st.rotation)
                .scale(st.scale);

        auxMat4.mul(st.localMatrix);
        st.globalMatrix.set(auxMat4);
        st.registerChange();
        st.freezeVersion();
        for(var comp : st.entity.components.values()){
            comp.registerChange();
        }
    }

    public float getDistanceFromCamera(Vector3f translation) {
        distanceAux.set(cameraRepository.currentCamera.position);
        return Math.abs(distanceAux.sub(translation).length());
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
