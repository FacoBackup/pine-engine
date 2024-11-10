package com.pine.service.rendering;

import com.pine.component.TransformationComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.service.grid.HashGridService;
import com.pine.service.grid.Tile;
import com.pine.service.grid.TileWorld;
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
    public HashGridService hashGridService;

    private final Vector3f distanceAux = new Vector3f();
    private final Matrix4f auxMat4 = new Matrix4f();
    private final Matrix4f auxMat42 = new Matrix4f();

    @Override
    protected void tickInternal() {
        startTracking();
        try {
            for (var tile : hashGridService.getLoadedTiles()) {
                if (tile != null) {
                    traverse(tile, tile.getWorld().rootEntity.id(), false);
                }
            }
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
        endTracking();
    }

    public void traverse(Tile tile, String entityId, boolean parentHasChanged) {
        TransformationComponent st = tile.getWorld().bagTransformationComponent.get(entityId);
        if (st != null && (st.isNotFrozen() || parentHasChanged)) {
            TransformationComponent parentTransform = findParent(tile, st.getEntityId());
            transform(tile, st, parentTransform);
            st.freezeVersion();
            parentHasChanged = true;
        }

        var children = tile.getWorld().parentChildren.get(entityId);
        if (children != null) {
            for (var child : children) {
                traverse(tile, child, parentHasChanged);
            }
        }
    }

    private void transform(Tile previousTile, TransformationComponent st, TransformationComponent parentTransform) {
        if (parentTransform != null) {
            auxMat4.set(parentTransform.modelMatrix);
        } else {
            auxMat4.identity();
        }

        auxMat42.identity();
        auxMat42
                .translate(st.translation)
                .rotate(st.rotation)
                .scale(st.scale);

        auxMat4.mul(auxMat42);
        st.modelMatrix.set(auxMat4);
        st.freezeVersion();
        hashGridService.moveEntityBetweenTiles(previousTile, st.translation, st.getEntityId());
    }

    private TransformationComponent findParent(Tile tile, String id) {
        if (tile != null && tile.getWorld().entityMap.containsKey(id)) {
            while (id != null && !id.equals(tile.getWorld().rootEntity.id())) {
                id = tile.getWorld().childParent.get(id);
                var t = tile.getWorld().bagTransformationComponent.get(id);
                if (t != null) {
                    return t;
                }
            }
        }
        return null;
    }

    public float getDistanceFromCamera(Vector3f translation) {
        distanceAux.set(cameraRepository.currentCamera.position);
        return distanceAux.sub(translation).length();
    }

    public boolean isCulled(Vector3f translation, float maxDistanceFromCamera, float cullingSphereRadius) {
        if (getDistanceFromCamera(translation) > maxDistanceFromCamera) {
            return true;
        }
        return !cameraRepository.frustum.isSphereInsideFrustum(translation, cullingSphereRadius);
    }

    @Override
    public String getTitle() {
        return "Transformation";
    }
}
