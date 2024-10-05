package com.pine.service.system.impl;

import com.pine.repository.rendering.RenderingMode;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.system.AbstractSystem;

public class VoxelVisualizerSystem extends AbstractSystem {

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.auxBuffer;
    }

    @Override
    protected void renderInternal() {
        shaderService.bind(shaderRepository.debugVoxelShader);
        ssboService.bind(ssboRepository.voxelGridSSBO);
        meshService.bind(primitiveRepository.cubeMesh);
        meshService.setRenderingMode(RenderingMode.TRIANGLES);
        meshService.setInstanceCount(10000);
        meshService.draw();
    }
}
