package com.pine.engine;

import com.pine.common.resource.IResourceService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Engine {
    private long since = 0;
    private long elapsedTime = 0;

    @Autowired
    private WorldRepository world;

    @Autowired
    private IResourceService resourceService;

    @PostConstruct
    public void init() throws RuntimeException {
//        resourceService.addResource(new ShaderCreationDTO("shaders/SPRITE.vert", "shaders/SPRITE.frag", "sprite"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/V_BUFFER.vert", "shaders/V_BUFFER.frag", "visibility"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/TO_SCREEN.frag", "toScreen"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BILINEAR_DOWNSCALE.glsl", "downscale"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BILATERAL_BLUR.glsl", "bilateralBlur"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BOKEH.frag", "bokeh"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/CUBEMAP.vert", "shaders/IRRADIANCE_MAP.frag", "irradiance"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/CUBEMAP.vert", "shaders/PREFILTERED_MAP.frag", "prefiltered"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/SSGI.frag", "ssgi"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/MOTION_BLUR.frag", "mb"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/SSAO.frag", "ssao"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BOX-BLUR.frag", "boxBlur"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/SHADOWS.vert", "shaders/DIRECTIONAL_SHADOWS.frag", "directShadows"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/SHADOWS.vert", "shaders/OMNIDIRECTIONAL_SHADOWS.frag", "omniDirectShadows"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/FRAME_COMPOSITION.frag", "composition"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BRIGHTNESS_FILTER.frag", "bloom"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/LENS_POST_PROCESSING.frag", "lens"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/GAUSSIAN.frag", "gaussian"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/UPSAMPLE_TENT.glsl", "upSampling"));
//        resourceService.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/ATMOSPHERE.frag", "atmosphere"));
    }

    public void render() {
        long newSince = System.currentTimeMillis();
        elapsedTime += newSince - since;
        since = newSince;

        world.process();
    }

    public long getElapsedTime() {
        return elapsedTime;
    }
}
