package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.voxelization.util.TextureUtil;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import static com.pine.service.resource.ShaderService.COMPUTE_RUNTIME_DATA;

public class CopyDepthPass extends AbstractQuadPassPass {
    @Override
    protected FrameBufferObject getTargetFBO() {
        return bufferRepository.sceneDepthCopy;
    }

    @Override
    protected boolean shouldClearFBO() {
        return true;
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.copyQuadShader;
    }

    @Override
    protected void bindUniforms() {
        shaderService.bindSampler2dDirect(bufferRepository.gBufferDepthIndexSampler, 0);
    }

    @Override
    public String getTitle() {
        return "Copy depth";
    }
}
