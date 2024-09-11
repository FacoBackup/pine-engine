package com.pine.engine.tools.repository;

import com.pine.engine.Engine;
import com.pine.engine.core.service.EngineInjectable;
import com.pine.engine.core.service.resource.ResourceService;
import com.pine.engine.core.service.resource.shader.Shader;
import com.pine.engine.core.service.resource.shader.ShaderCreationData;

public class ToolsResourceRepository implements EngineInjectable {
    private ResourceService resources;
    public Shader gridShader;

    @Override
    public void setEngine(Engine engine) {
        this.resources = engine.getResourcesService();
    }

    @Override
    public void onInitialize() {
        gridShader = (Shader) resources.addResource(new ShaderCreationData("shaders/GRID.vert", "shaders/GRID.frag", "grid"));
    }
}
