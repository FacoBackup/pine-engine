package com.pine.repository.terrain;

import com.pine.inspection.InspectableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.theme.Icons;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class FoliageInstance extends AbstractDataInstance {
    public int count = 0;
    public int offset = 0;

    @ResourceTypeField(type = StreamableResourceType.MATERIAL)
    @InspectableField(label = "Material")
    public String material;

    @ResourceTypeField(type = StreamableResourceType.MESH)
    @InspectableField(label = "Mesh")
    public String mesh;

    @InspectableField(label = "Max distance from camera", min = 1)
    public int maxDistanceFromCamera = 100;
    @InspectableField(label = "Max instances per cell (squared) ", min = 1, max = 15)
    public int maxIterations = 5;
    @InspectableField(label = "Instance offset scale")
    public Vector2f instanceOffset = new Vector2f(5);

    @InspectableField(label = "Object Scale")
    public Vector3f objectScale = new Vector3f(1);

    public FoliageInstance(int i) {
        super(i);
        name = "Foliage instance " + i;
    }

    @Override
    public String getIcon() {
        return Icons.forest;
    }
}
