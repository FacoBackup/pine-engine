package com.pine.engine.core.service.resource;

import com.pine.common.EngineComponent;
import com.pine.common.Loggable;
import com.pine.engine.Engine;
import com.pine.engine.core.ClockRepository;
import com.pine.engine.core.RuntimeRepository;
import com.pine.engine.core.gl.shader.ShaderCreationData;
import com.pine.engine.core.service.resource.resource.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResourceService implements EngineComponent, Loggable {
    public static final long MAX_TIMEOUT = 5 * 60 * 1000;
    private final Map<String, IResource> resources = new HashMap<>();
    private final Map<String, Long> sinceLastUse = new HashMap<>();
    private final Map<ResourceType, List<String>> usedResources = new HashMap<>();
    private final List<AbstractResourceService<? extends IResource, ? extends IResourceRuntimeData, ? extends ResourceCreationData>> implementations = List.of(
            new AudioService(),
            new MeshService(),
            new ShaderService(),
            new TextureService(),
            new UBOService()
    );
    private final ClockRepository clock;
    private final RuntimeRepository runtimeRepository;
    private long sinceLastCleanup = 0;

    public ResourceService(Engine engine) {
        this.clock = engine.getClock();
        this.runtimeRepository = engine.getRuntimeRepository();
    }

    public IResource addResource(ResourceCreationData data) {
        IResource instance = null;
        for (var i : implementations) {
            if (i.getResourceType() == data.getResourceType()) {
                instance = i.add(data);
            }
        }
        if (instance == null) {
            getLogger().warn("Resource could not be initialized correctly: {}", data.getResourceType());
            return null;
        }
        resources.put(instance.getId(), instance);
        sinceLastUse.put(instance.getId(), System.currentTimeMillis());
        return instance;
    }

    public void removeResource(String id) {
        IResource resource = resources.get(id);
        if (resource == null) {
            getLogger().warn("Resource not found: {}", id);
            return;
        }

        if (resource.isStatic()) {
            return;
        }

        for (var i : implementations) {
            if (i.getResourceType() == resource.getResourceType()) {
                i.remove(resource);
            }
        }
    }

    public <T extends IResource, R extends IResourceRuntimeData> void bind(T instance, R data) {
        for (var i : implementations) {
            if (i.getResourceType() == instance.getResourceType()) {
                i.bind(instance, data);
            }
        }
    }

    public <T extends IResource> void bind(T instance) {
        for (var i : implementations) {
            if (i.getResourceType() == instance.getResourceType()) {
                i.bind(instance);
            }
        }
    }

    public List<IResource> getAllByType(ResourceType type) {
        return resources.values().stream().filter(r -> r.getResourceType().equals(type)).collect(Collectors.toList());
    }

    public void shutdown() {
        implementations.forEach(i -> i.shutdown(getAllByType(i.getResourceType())));
    }

    @Override
    public void tick() {
        if ((clock.totalTime - sinceLastCleanup) >= MAX_TIMEOUT) {
            sinceLastCleanup = clock.totalTime;
            int removed = 0;
            for (var entry : sinceLastUse.entrySet()) {
                if (System.currentTimeMillis() - entry.getValue() > MAX_TIMEOUT) {
                    removeResource(entry.getKey());
                    removed++;
                }
            }
            getLogger().warn("Removed {} unused resources", removed);
            usedResources.clear();
            resources.values().forEach(resource -> {
                usedResources.putIfAbsent(resource.getResourceType(), new ArrayList<>());
                usedResources.get(resource.getResourceType()).add(resource.getId());
            });
        }
    }

    public AudioService getAudioService() {
        return (AudioService) this.implementations.getFirst();
    }

    public MeshService getMeshService() {
        return (MeshService) this.implementations.get(1);
    }

    public ShaderService getShaderService() {
        return (ShaderService) this.implementations.get(2);
    }

    public TextureService getTextureService() {
        return (TextureService) this.implementations.get(3);
    }

    public UBOService getUBOService() {
        return (UBOService) this.implementations.get(4);
    }

    @Override
    public void onInitialize() {
        runtimeRepository.spriteShaderId = addResource(new ShaderCreationData("shaders/SPRITE.vert", "shaders/SPRITE.frag", "sprite")).getId();
        runtimeRepository.visibilityShaderId = addResource(new ShaderCreationData("shaders/V_BUFFER.vert", "shaders/V_BUFFER.frag", "visibility")).getId();
        runtimeRepository.toScreenShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/TO_SCREEN.frag", "toScreen")).getId();
        runtimeRepository.downscaleShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BILINEAR_DOWNSCALE.glsl", "downscale")).getId();
        runtimeRepository.bilateralBlurShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BILATERAL_BLUR.glsl", "bilateralBlur")).getId();
        runtimeRepository.bokehShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BOKEH.frag", "bokeh")).getId();
        runtimeRepository.irradianceShaderId = addResource(new ShaderCreationData("shaders/CUBEMAP.vert", "shaders/IRRADIANCE_MAP.frag", "irradiance")).getId();
        runtimeRepository.prefilteredShaderId = addResource(new ShaderCreationData("shaders/CUBEMAP.vert", "shaders/PREFILTERED_MAP.frag", "prefiltered")).getId();
        runtimeRepository.ssgiShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/SSGI.frag", "ssgi")).getId();
        runtimeRepository.mbShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/MOTION_BLUR.frag", "mb")).getId();
        runtimeRepository.ssaoShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/SSAO.frag", "ssao")).getId();
        runtimeRepository.boxBlurShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BOX-BLUR.frag", "boxBlur")).getId();
        runtimeRepository.directShadowsShaderId = addResource(new ShaderCreationData("shaders/SHADOWS.vert", "shaders/DIRECTIONAL_SHADOWS.frag", "directShadows")).getId();
        runtimeRepository.omniDirectShadowsShaderId = addResource(new ShaderCreationData("shaders/SHADOWS.vert", "shaders/OMNIDIRECTIONAL_SHADOWS.frag", "omniDirectShadows")).getId();
        runtimeRepository.compositionShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/FRAME_COMPOSITION.frag", "composition")).getId();
        runtimeRepository.bloomShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BRIGHTNESS_FILTER.frag", "bloom")).getId();
        runtimeRepository.lensShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/LENS_POST_PROCESSING.frag", "lens")).getId();
        runtimeRepository.gaussianShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/GAUSSIAN.frag", "gaussian")).getId();
        runtimeRepository.upSamplingShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/UPSAMPLE_TENT.glsl", "upSampling")).getId();
        runtimeRepository.atmosphereShaderId = addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/ATMOSPHERE.frag", "atmosphere")).getId();

        runtimeRepository.gridShaderId = addResource(new ShaderCreationData("shaders/GRID.vert", "shaders/GRID.frag", "grid")).getId();
    }

    public IResource getById(String id) {
        return resources.get(id);
    }
}
