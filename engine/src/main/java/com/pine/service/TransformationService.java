package com.pine.service;

import com.pine.EngineUtils;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.Entity;
import com.pine.component.TransformationComponent;
import com.pine.repository.CameraRepository;
import com.pine.repository.CoreSSBORepository;
import com.pine.repository.RenderingRepository;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static com.pine.tasks.RenderingTask.TRANSFORMATION_COMP;

@PBean
public class TransformationService {
    private static final float TO_RAD = (float) (Math.PI / 180);
    @PInject
    public CameraRepository camera;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public CoreSSBORepository ssboRepository;

    private final Vector3f rotationAux = new Vector3f();
    private final Vector3f distanceAux = new Vector3f();
    private final Vector3f auxCubeMax = new Vector3f();
    private final Vector3f auxCubeMin = new Vector3f();
    private final Matrix4f auxMat4 = new Matrix4f();

    public void updateMatrix(TransformationComponent st){
        auxMat4.identity();
        Entity parent = st.entity.parent;
        TransformationComponent parentTransform = null;
        while (parent != null) {
            if (parent.components.containsKey(TRANSFORMATION_COMP)) {
                parentTransform = (TransformationComponent) parent.components.get(TRANSFORMATION_COMP);
                break;
            }
            parent = parent.parent;
        }

        updateMatrix(st, parentTransform);
    }

    public void updateMatrix(TransformationComponent st, TransformationComponent parentTransform) {
        if (parentTransform != null) {
            auxMat4.set(parentTransform.matrix);
            if (parentTransform.getChangeId() != st.parentChangeId || !st.isFrozen()) {
                st.parentChangeId = parentTransform.getChangeId();
                transform(st);
            }
        } else if (!st.isFrozen()) {
            transform(st);
        }
    }

    public void extractTransformations(TransformationComponent st) {
        EngineUtils.copyWithOffset(ssboRepository.transformationSSBOState, st.matrix, renderingRepository.offset);
        renderingRepository.offset += 16;
    }

    private void transform(TransformationComponent st) {
        st.matrix.identity();
        st.matrix
                .translate(st.translation)
                .rotate(st.rotation)
                .scale(st.scale);

        auxMat4.mul(st.matrix);
        st.matrix.set(auxMat4);
        st.registerChange();
        st.freezeVersion();
    }

    public boolean isCulled(Vector3f translation, float maxDistanceFromCamera, Vector3f frustumBoxDimensions) {
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
