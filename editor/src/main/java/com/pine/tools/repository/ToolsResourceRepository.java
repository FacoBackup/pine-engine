package com.pine.tools.repository;

import com.pine.Initializable;
import com.pine.core.EngineDependency;
import com.pine.core.service.resource.ResourceService;
import com.pine.core.service.resource.shader.Shader;
import com.pine.core.service.resource.shader.ShaderCreationData;

public class ToolsResourceRepository implements Initializable {

    @EngineDependency
    public ResourceService resources;

    public Shader gridShader;

    @Override
    public void onInitialize() {
        gridShader = (Shader) resources.addResource(new ShaderCreationData("shaders/GRID.vert", "shaders/GRID.frag", "grid"));
    }
}
