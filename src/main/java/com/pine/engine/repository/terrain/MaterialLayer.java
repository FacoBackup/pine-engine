package com.pine.engine.repository.terrain;

import com.pine.common.Icons;
import com.pine.common.inspection.Inspectable;
import com.pine.common.inspection.InspectableField;
import com.pine.engine.inspection.ResourceTypeField;
import com.pine.engine.repository.streaming.StreamableResourceType;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MaterialLayer extends Inspectable {

    public final Vector4f channel;

    @InspectableField(label = "Layer name")
    public String name;

    @ResourceTypeField(type = StreamableResourceType.MATERIAL)
    @InspectableField(label = "Material")
    public String material;

    @InspectableField(label = "Weight")
    public float weight = 1;

    public int channelFlag;

    public MaterialLayer(int channel) {
        channelFlag = channel;
        if (channel == 0) {
            this.channel = new Vector4f(1, 0, 0, 0);
        } else if (channel == 1) {
            this.channel = new Vector4f(0, 1, 0, 0);
        } else if (channel == 2) {
            this.channel = new Vector4f(0, 0, 1, 0);
        } else if (channel == 3) {
            this.channel = new Vector4f(0, 0, 0, 1);
        } else {
            this.channel = new Vector4f(0, 0, 0, 0);
        }

        name = "Material layer (" + channel + ")";
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getIcon() {
        return Icons.format_paint;
    }
}
