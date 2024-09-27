package com.pine.component.rendering;

import com.pine.inspection.MutableField;
import com.pine.inspection.Inspectable;
import com.pine.theme.Icons;
import org.joml.Vector3f;

import javax.swing.*;

public class SimpleTransformation extends Inspectable {
    @MutableField(label = "Translation")
    public Vector3f translation = new Vector3f();
    @MutableField(label = "Scale")
    public Vector3f scale = new Vector3f(1);
    @MutableField(label = "Rotation")
    public Vector3f rotation = new Vector3f();

    public int parentTransformationId = -1;
    public final int parentEntityId;
    public int primitiveIndex;

    public SimpleTransformation(int parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    @Override
    public String getTitle() {
        return "Transformation";
    }

    @Override
    public String getIcon() {
        return Icons.control_camera;
    }
}
