package com.pine.core;

import com.pine.core.service.repository.MeshRepository;
import com.pine.core.service.repository.ShaderRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Engine {
    private long elapsedTime = 0;

    @Autowired
    private WorldRepository world;

    @Autowired
    private ShaderRepository shaders;

    @Autowired
    private MeshRepository meshes;

    @PostConstruct
    public void init() throws RuntimeException {
//        StaticShadersState.terrain = new Shader("shaders/TERRAIN.vert", "shaders/TERRAIN.frag")
//        StaticShadersState.sprite = new Shader("shaders/SPRITE.vert", "shaders/SPRITE.frag")
//        StaticShadersState.visibility = new Shader("shaders/V_BUFFER.vert", "shaders/V_BUFFER.frag")
//        StaticShadersState.toScreen = new Shader("shaders/QUAD.vert", "shaders/TO_SCREEN.frag")
//        StaticShadersState.downscale = new Shader("shaders/QUAD.vert", "shaders/BILINEAR_DOWNSCALE.glsl")
//        StaticShadersState.bilateralBlur = new Shader("shaders/QUAD.vert", "shaders/BILATERAL_BLUR.glsl")
//        StaticShadersState.bokeh = new Shader("shaders/QUAD.vert", "shaders/BOKEH.frag")
//        StaticShadersState.irradiance = new Shader("shaders/CUBEMAP.vert", "shaders/IRRADIANCE_MAP.frag")
//        StaticShadersState.prefiltered = new Shader("shaders/CUBEMAP.vert", "shaders/PREFILTERED_MAP.frag")
//        StaticShadersState.ssgi = new Shader("shaders/QUAD.vert", "shaders/SSGI.frag")
//        StaticShadersState.mb = new Shader("shaders/QUAD.vert", "shaders/MOTION_BLUR.frag")
//        StaticShadersState.ssao = new Shader("shaders/QUAD.vert", "shaders/SSAO.frag")
//        StaticShadersState.boxBlur = new Shader("shaders/QUAD.vert", "shaders/BOX-BLUR.frag")
//        StaticShadersState.directShadows = new Shader("shaders/SHADOWS.vert", "shaders/DIRECTIONAL_SHADOWS.frag")
//        StaticShadersState.omniDirectShadows = new Shader("shaders/SHADOWS.vert", "shaders/OMNIDIRECTIONAL_SHADOWS.frag")
//        StaticShadersState.composition = new Shader("shaders/QUAD.vert", "shaders/FRAME_COMPOSITION.frag")
//        StaticShadersState.bloom = new Shader("shaders/QUAD.vert", "shaders/BRIGHTNESS_FILTER.frag")
//        StaticShadersState.lens = new Shader("shaders/QUAD.vert", "shaders/LENS_POST_PROCESSING.frag")
//        StaticShadersState.gaussian = new Shader("shaders/QUAD.vert", "shaders/GAUSSIAN.frag")
//        StaticShadersState.upSampling = new Shader("shaders/QUAD.vert", "shaders/UPSAMPLE_TENT.glsl")
//        StaticShadersState.atmosphere = new Shader("shaders/QUAD.vert", "shaders/ATMOSPHERE.frag")

    }

    public void render() {

    }
}
