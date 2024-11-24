package com.pine.engine.service.rendering;

import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.engine.component.CullingComponent;
import com.pine.engine.component.TransformationComponent;
import com.pine.engine.repository.CameraRepository;
import com.pine.engine.repository.WorldRepository;
import com.pine.engine.repository.rendering.RenderingRepository;
import com.pine.engine.service.world.WorldService;
import com.pine.engine.tasks.AbstractTask;
import org.joml.Matrix4f;
import org.joml.Vector3f;


@PBean
public class TransformationService extends AbstractTask {
    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public WorldService worldService;

    @PInject
    public WorldRepository world;

    private final Vector3f distanceAux = new Vector3f();
    private final Matrix4f auxMat4 = new Matrix4f();
    private final Vector3f translation = new Vector3f();
    private final Matrix4f auxMat42 = new Matrix4f();

    @Override
    protected void tickInternal() {
        startTracking();
        try {
            for (var tile : worldService.getLoadedTiles()) {
                if (tile != null) {
                    traverse(WorldRepository.ROOT_ID, false);
                }
            }
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
        endTracking();
    }

    public void traverse(String entityId, boolean parentHasChanged) {
        TransformationComponent st = world.bagTransformationComponent.get(entityId);
        if (st != null && (st.isNotFrozen() || parentHasChanged)) {
            TransformationComponent parentTransform = findParent(st.getEntityId());
            transform(st, parentTransform);
            st.freezeVersion();
            parentHasChanged = true;
        }

        var children = world.parentChildren.get(entityId);
        if (children != null) {
            for (var child : children) {
                traverse(child, parentHasChanged);
            }
        }
    }

    private void transform(TransformationComponent st, TransformationComponent parentTransform) {
        if (parentTransform != null) {
            auxMat4.set(parentTransform.modelMatrix);
        } else {
            auxMat4.identity();
        }

        st.modelMatrix.getTranslation(translation);
        var previousTile = worldService.getHashGrid().getOrCreateTile(translation);

        auxMat42.identity();
        auxMat42
                .translate(st.translation)
                .rotate(st.rotation)
                .scale(st.scale);

        auxMat4.mul(auxMat42);
        st.modelMatrix.set(auxMat4);
        st.freezeVersion();


        st.modelMatrix.getTranslation(translation);
        var newTile = worldService.getHashGrid().getOrCreateTile(translation);

        worldService.getHashGrid().moveBetweenTiles(st.getEntityId(), previousTile, newTile);
    }

    private TransformationComponent findParent(String id) {
        while (id != null && !id.equals(WorldRepository.ROOT_ID)) {
            id = world.childParent.get(id);
            var t = world.bagTransformationComponent.get(id);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    public float getDistanceFromCamera(Vector3f translation) {
        distanceAux.set(cameraRepository.currentCamera.position);
        return distanceAux.sub(translation).length();
    }

    @Override
    public String getTitle() {
        return "Transformation";
    }

    public boolean isCulled(TransformationComponent transform, CullingComponent component) {
        component.distanceFromCamera = getDistanceFromCamera(transform.translation);
        if (component.distanceFromCamera > component.maxDistanceFromCamera) {
            return true;
        }
        return !cameraRepository.frustum.isSphereInsideFrustum(translation, component.cullingSphereRadius);
    }
}
