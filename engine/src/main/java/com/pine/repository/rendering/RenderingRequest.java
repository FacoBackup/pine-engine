package com.pine.repository.rendering;

import com.pine.component.TransformationComponent;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.MeshResourceRef;
import org.joml.Matrix4f;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class RenderingRequest implements Serializable {
    public transient MeshResourceRef mesh;
    public transient MaterialResourceRef material;
    public boolean isCulled;
    public int renderIndex;
    public String entity;
    public Matrix4f modelMatrix;
}
