package com.pine.engine;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.common.Renderable;
import com.pine.engine.core.ClockRepository;
import com.pine.engine.core.EnvRepository;
import com.pine.engine.core.components.system.ISystem;
import com.pine.engine.core.service.camera.CameraService;
import com.pine.engine.core.service.loader.ResourceLoader;
import com.pine.engine.core.service.resource.*;
import com.pine.engine.core.service.serialization.SerializableRepository;
import com.pine.engine.core.service.world.WorldService;


public class Engine extends SerializableRepository implements Renderable {
    private final ClockRepository clock = new ClockRepository();
    private final EnvRepository envRepository = new EnvRepository();
    private final CameraService camera = new CameraService(this);
    private final WorldService world = new WorldService();
    private final ResourceService resources = new ResourceService(clock);
    private final ResourceLoader loader = new ResourceLoader(this);

    @Override
    public void onInitialize() {
        resources.onInitialize();
        camera.onInitialize();
        loader.onInitialize();
        for (var sys : world.getSystems()) {
            sys.setEngine(this);
        }
    }

    @Override
    public void tick() {
        clock.tick();
        camera.tick();
        resources.tick();
        loader.tick();
        world.getSystems().forEach(ISystem::tick);
    }

    @Override
    public void render() {
        world.getWorld().process();
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

    public ResourceLoader getLoader() {
        return loader;
    }
}
