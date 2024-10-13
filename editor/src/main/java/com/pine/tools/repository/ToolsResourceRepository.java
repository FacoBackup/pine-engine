package com.pine.tools.repository;

import com.pine.injection.PInject;
import com.pine.service.module.Initializable;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.ShaderCreationData;

import static com.pine.service.resource.shader.ShaderCreationData.LOCAL_SHADER;

public class ToolsResourceRepository implements Initializable {

    @PInject
    public ResourceService resources;

    public Shader gridShader;

    @Override
    public void onInitialize() {
        gridShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "tool/GRID.vert", LOCAL_SHADER + "tool/GRID.frag"));
    }
}
