package com.pine.core;

import com.pine.core.service.ResourceRepository;
import com.pine.core.service.repository.MeshRepository;
import com.pine.core.service.repository.ShaderRepository;
import com.pine.core.service.repository.primitives.shader.ShaderCreationDTO;
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
    private ResourceRepository resources;

    @PostConstruct
    public void init() throws RuntimeException {
        resources.addResource(new ShaderCreationDTO("shaders/TERRAIN.vert", "shaders/TERRAIN.frag", "terrain"));
        resources.addResource(new ShaderCreationDTO("shaders/SPRITE.vert", "shaders/SPRITE.frag", "sprite"));
        resources.addResource(new ShaderCreationDTO("shaders/V_BUFFER.vert", "shaders/V_BUFFER.frag", "visibility"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/TO_SCREEN.frag", "toScreen"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BILINEAR_DOWNSCALE.glsl", "downscale"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BILATERAL_BLUR.glsl", "bilateralBlur"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BOKEH.frag", "bokeh"));
        resources.addResource(new ShaderCreationDTO("shaders/CUBEMAP.vert", "shaders/IRRADIANCE_MAP.frag", "irradiance"));
        resources.addResource(new ShaderCreationDTO("shaders/CUBEMAP.vert", "shaders/PREFILTERED_MAP.frag", "prefiltered"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/SSGI.frag", "ssgi"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/MOTION_BLUR.frag", "mb"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/SSAO.frag", "ssao"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BOX-BLUR.frag", "boxBlur"));
        resources.addResource(new ShaderCreationDTO("shaders/SHADOWS.vert", "shaders/DIRECTIONAL_SHADOWS.frag", "directShadows"));
        resources.addResource(new ShaderCreationDTO("shaders/SHADOWS.vert", "shaders/OMNIDIRECTIONAL_SHADOWS.frag", "omniDirectShadows"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/FRAME_COMPOSITION.frag", "composition"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BRIGHTNESS_FILTER.frag", "bloom"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/LENS_POST_PROCESSING.frag", "lens"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/GAUSSIAN.frag", "gaussian"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/UPSAMPLE_TENT.glsl", "upSampling"));
        resources.addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/ATMOSPHERE.frag", "atmosphere"));
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
