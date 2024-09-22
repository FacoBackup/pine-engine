package com.pine.tools.repository;

import com.pine.Initializable;
import com.pine.PInject;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.shader.ShaderCreationData;
import com.pine.service.resource.shader.Shader;

import static com.pine.service.resource.shader.ShaderCreationData.LOCAL_SHADER;

public class ToolsResourceRepository implements Initializable {

    @PInject
    public ResourceService resources;

    public Shader gridShader;

    @Override
    public void onInitialize() {
        gridShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "GRID.vert", LOCAL_SHADER + "GRID.frag"));
    }
}
