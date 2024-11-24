package com.pine.engine.repository.rendering;

import com.pine.engine.service.streaming.ref.MaterialResourceRef;
import com.pine.engine.service.streaming.ref.MeshResourceRef;
import org.joml.Matrix4f;

import java.io.Serializable;

public class RenderingRequest implements Serializable {
    public transient MeshResourceRef mesh;
    public transient MaterialResourceRef material;
    public boolean isCulled;
    public String entity;
    public Matrix4f modelMatrix;
}
