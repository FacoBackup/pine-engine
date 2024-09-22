package com.pine.service.system;


import com.pine.Engine;
import com.pine.Initializable;
import com.pine.PInject;
import com.pine.repository.*;
import com.pine.service.resource.*;
import com.pine.service.resource.fbo.FrameBufferObject;

public abstract class AbstractSystem implements Initializable {
    @PInject
    public Engine engine;
    @PInject
    public CameraRepository cameraRepository;
    @PInject
    public RenderingRepository renderingRepository;
    @PInject
    public UBOService uboService;
    @PInject
    public ComputeService computeService;
    @PInject
    public ShaderService shaderService;
    @PInject
    public SSBOService ssboService;
    @PInject
    public MeshService meshService;
    @PInject
    public CoreShaderRepository shaderRepository;
    @PInject
    public CoreSSBORepository ssboRepository;
    @PInject
    public CoreUBORepository uboRepository;
    @PInject
    public CoreFBORepository fboRepository;
    @PInject
    public CoreComputeRepository computeRepository;
    @PInject
    public CorePrimitiveRepository primitiveRepository;

    final public void render() {
        if (!isRenderable()) {
            return;
        }

        FrameBufferObject fbo = getTargetFBO();
        if (fbo != null) {
            fbo.startMapping(shouldClearFBO());
            renderInternal();
            fbo.stop();
        } else {
            renderInternal();
        }
    }

    protected FrameBufferObject getTargetFBO() {
        return null;
    }

    protected void renderInternal() {

    }

    protected boolean isRenderable() {
        return true;
    }

    protected boolean shouldClearFBO() {
        return false;
    }

    @Override
    public void onInitialize() {
    }
}
