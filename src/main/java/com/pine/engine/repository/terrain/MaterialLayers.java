package com.pine.engine.repository.terrain;

import com.pine.common.Icons;
import com.pine.common.inspection.Inspectable;
import com.pine.common.inspection.InspectableField;

public class MaterialLayers extends Inspectable {

    public static final int MAX_LAYERS = 4;
    @InspectableField(group = "Materials", label = "Material A")
    public final MaterialLayer materialLayerA = new MaterialLayer(0);

    @InspectableField(group = "Materials", label = "Material B")
    public final MaterialLayer materialLayerB = new MaterialLayer(1);

    @InspectableField(group = "Materials", label = "Material C")
    public final MaterialLayer materialLayerC = new MaterialLayer(2);

    @InspectableField(group = "Materials", label = "Material D")
    public final MaterialLayer materialLayerD = new MaterialLayer(3);

    @Override
    public String getTitle() {
        return "Materials";
    }

    @Override
    public String getIcon() {
        return Icons.format_paint;
    }

    public MaterialLayer getLayer(int i) {
        if (i == 0) return materialLayerA;
        else if (i == 1) return materialLayerB;
        else if (i == 2) return materialLayerC;
        else if (i == 3) return materialLayerD;
        return null;
    }
}
