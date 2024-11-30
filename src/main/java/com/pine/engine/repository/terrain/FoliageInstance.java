package com.pine.engine.repository.terrain;

import com.pine.common.Icons;
import com.pine.common.injection.Disposable;
import com.pine.common.inspection.Inspectable;
import com.pine.common.inspection.InspectableField;
import com.pine.engine.inspection.ResourceTypeField;
import com.pine.engine.repository.streaming.StreamableResourceType;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

public class FoliageInstance extends Inspectable implements Disposable {
    @InspectableField(label = "Terrain material index", min = 0, max = 3)
    public int materialMaskIndex = 0;

    @InspectableField(label = "Name")
    public String name;

    @ResourceTypeField(type = StreamableResourceType.MATERIAL)
    @InspectableField(label = "Material")
    public String material;

    @ResourceTypeField(type = StreamableResourceType.MESH)
    @InspectableField(label = "Mesh")
    public String mesh;

    @InspectableField(label = "Max instances", min = 10)
    public int maximumNumberOfInstances = 10_000;
    public int prevMaximumNumberOfInstances = -1;

    @InspectableField(label = "Max distance from camera", min = 1)
    public int maxDistanceFromCamera = 100;

    @InspectableField(label = "Object Scale")
    public Vector3f objectScale = new Vector3f(1);

    public transient Integer indirectDrawBuffer;
    public transient Integer atomicCounterBuffer;
    public transient Integer transformationsBuffer;

    public FoliageInstance() {
        name = "New foliage instance";
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getIcon() {
        return Icons.forest;
    }

    @Override
    public void dispose() {
        if (transformationsBuffer != null) {
            GL46.glDeleteBuffers(indirectDrawBuffer);
            GL46.glDeleteBuffers(atomicCounterBuffer);
            GL46.glDeleteBuffers(transformationsBuffer);
        }
    }
}
