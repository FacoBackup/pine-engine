package com.pine.engine;

import com.pine.engine.core.components.system.ISystem;
import com.pine.engine.core.service.*;

import java.util.List;


public class Engine {
    private final long startupTime = System.currentTimeMillis();
    private long since = 0;
    private long elapsedTime = 0;
    private long totalTime = 0;
    private boolean inputFocused = false;

    private final CameraService camera = new CameraService(this);
    private final WorldService world = new WorldService();
    private final AudioService audioService = new AudioService();
    private final MaterialService materialService = new MaterialService();
    private final MeshService meshService = new MeshService();
    private final ShaderService shaderService = new ShaderService();
    private final TextureService textureService = new TextureService();
    private final UBOService uboService = new UBOService();
    private final ResourceService resources = new ResourceService(List.of(
            audioService,
            materialService,
            meshService,
            shaderService,
            textureService,
            uboService
    ));

    public void onInitialize() {
        resources.onInitialize();
        for (var sys : world.getWorld().getSystems()) {
            ((ISystem) sys).setEngine(this);
        }
    }

    public void tick() {
        camera.tick();
        resources.removeUnused(totalTime);
        for (var sys : world.getWorld().getSystems()) {
            ((ISystem) sys).tick();
        }
    }

    public void render() {
        long newSince = System.currentTimeMillis();
        totalTime = newSince - startupTime;
        elapsedTime += newSince - since;
        since = newSince;
        world.getWorld().process();
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void shutdown() {
        resources.shutdown();
        world.getWorld().dispose();
    }

    public ResourceService getResources() {
        return resources;
    }

    public WorldService getWorld() {
        return world;
    }

    public boolean isInputFocused() {
        return inputFocused;
    }

    public void setInputFocused(boolean inputFocused) {
        this.inputFocused = inputFocused;
    }

    public float getTotalTime() {
        return totalTime;
    }
}
