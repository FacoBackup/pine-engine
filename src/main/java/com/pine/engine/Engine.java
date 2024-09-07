package com.pine.engine;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pine.common.Renderable;
import com.pine.engine.core.ClockRepository;
import com.pine.engine.core.EnvRepository;
import com.pine.engine.core.components.system.ISystem;
import com.pine.engine.core.service.*;

import java.util.List;


public class Engine implements Renderable {
    private final ClockRepository clock = new ClockRepository();
    private final EnvRepository envRepository = new EnvRepository();
    private final CameraService camera = new CameraService(this);
    private final WorldService world = new WorldService();
    private final AudioService audioService = new AudioService();
    private final MaterialService materialService = new MaterialService();
    private final MeshService meshService = new MeshService();
    private final ShaderService shaderService = new ShaderService();
    private final TextureService textureService = new TextureService();
    private final UBOService uboService = new UBOService();
    private final ResourceService resources = new ResourceService(
            List.of(
                    audioService,
                    materialService,
                    meshService,
                    shaderService,
                    textureService,
                    uboService
            ), clock
    );

    @Override
    public void onInitialize() {
        resources.onInitialize();
        camera.onInitialize();
        for (var sys : world.getSystems()) {
            sys.setEngine(this);
        }
    }

    @Override
    public void tick() {
        clock.tick();
        camera.tick();
        resources.tick();
        world.getSystems().forEach(ISystem::tick);
    }

    @Override
    public void render() {
        world.getWorld().process();
    }

    public void shutdown() {
        resources.shutdown();
        world.getWorld().dispose();
    }

    public void parse(String serialized) {
        try {
            world.parse(new Gson().fromJson(serialized, JsonObject.class));
        } catch (Exception e) {
            getLogger().error("Failed to parse world", e);
        }
    }

    public String serialize() {
        return world.serialize().toString();
    }

    public CameraService getCameraService() {
        return camera;
    }

    public EnvRepository getInputRepository() {
        return envRepository;
    }

    public ResourceService getResources() {
        return resources;
    }

    public WorldService getWorld() {
        return world;
    }

    public ClockRepository getClock() {
        return clock;
    }
}
