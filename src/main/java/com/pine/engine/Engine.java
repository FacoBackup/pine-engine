package com.pine.engine;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.common.Renderable;
import com.pine.engine.core.ClockRepository;
import com.pine.engine.core.ConfigurationRepository;
import com.pine.engine.core.RuntimeRepository;
import com.pine.engine.core.service.camera.CameraService;
import com.pine.engine.core.service.loader.ResourceLoader;
import com.pine.engine.core.service.resource.*;
import com.pine.engine.core.service.serialization.SerializableRepository;
import com.pine.engine.core.service.world.WorldService;


public class Engine extends SerializableRepository implements Renderable {
    transient private final ClockRepository clock = new ClockRepository();
    transient private final RuntimeRepository runtimeRepository = new RuntimeRepository();
    transient private ExecutionEnvironment env = ExecutionEnvironment.DEVELOPMENT;
    private final CameraService camera = new CameraService(this);
    private final WorldService world = new WorldService(this);
    private final ResourceService resources = new ResourceService(this);
    private final ResourceLoader loader = new ResourceLoader(this);
    private final ConfigurationRepository config = new ConfigurationRepository();

    @Override
    public void onInitialize() {
        resources.onInitialize();
        camera.onInitialize();
        loader.onInitialize();
        world.onInitialize();
    }

    @Override
    public void tick() {
        clock.tick();
        camera.tick();
        resources.tick();
        loader.tick();
        world.tick();
    }

    @Override
    public void render() {
        world.render();
    }

    public void shutdown() {
        resources.shutdown();
        world.shutdown();
    }

    public void parse(String serialized) {
        try {
            world.parse(new Gson().fromJson(serialized, JsonObject.class));
        } catch (Exception e) {
            getLogger().error("Failed to parse world", e);
        }
    }

    @Override
    public JsonElement serializeData() {
        JsonArray arr = new JsonArray();
        arr.add(world.serialize().toString());
        arr.add(camera.serialize().toString());
        arr.add(loader.serialize().toString());
        return arr;
    }

    @Override
    protected void parseInternal(JsonElement data) {
        JsonArray json = data.getAsJsonArray();
        json.forEach(a -> {
            JsonObject obj = a.getAsJsonObject();
            if (world.isCompatible(obj)) {
                world.parse(obj);
            }
            if (loader.isCompatible(obj)) {
                loader.parse(obj);
            }
            if (camera.isCompatible(obj)) {
                camera.parse(obj);
            }
        });
    }

    public CameraService getCameraService() {
        return camera;
    }

    public RuntimeRepository getRuntimeRepository() {
        return runtimeRepository;
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

    public ResourceLoader getLoader() {
        return loader;
    }

    public ExecutionEnvironment getEnvironment() {
        return env;
    }

    public void setEnvironment(ExecutionEnvironment env) {
        this.env = env;
    }

    public ConfigurationRepository getConfigurationRepository() {
        return config;
    }
}
