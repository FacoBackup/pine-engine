package com.pine.engine.tools.repository;

import com.pine.common.Initializable;
import com.pine.engine.core.service.resource.ResourceService;
import com.pine.engine.core.service.resource.shader.Shader;
import com.pine.engine.core.service.resource.shader.ShaderCreationData;
import com.pine.engine.core.EngineDependency;

public class ToolsResourceRepository implements Initializable {

    @EngineDependency
    public ResourceService resources;

    public Shader gridShader;

    @Override
    public void onInitialize() {
        gridShader = (Shader) resources.addResource(new ShaderCreationData("shaders/GRID.vert", "shaders/GRID.frag", "grid"));
    }
}
