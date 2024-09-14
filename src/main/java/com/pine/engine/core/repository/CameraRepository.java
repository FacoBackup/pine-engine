package com.pine.engine.core.repository;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.common.Initializable;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.service.camera.AbstractCamera;
import com.pine.engine.core.service.camera.OrthographicCamera;
import com.pine.engine.core.service.camera.PerspectiveCamera;
import com.pine.engine.core.service.serialization.SerializableRepository;

import java.util.HashMap;
import java.util.Map;

@EngineInjectable
public class CameraRepository extends SerializableRepository {
    private static final Gson GSON = new Gson();

    public float pitch = 0.0f;
    public float yaw = -90.0f;
    public float sensitivity = 0.1f;
    transient public float lastMouseX;
    transient public float lastMouseY;
    public boolean firstMouseMove = true;
    public float movementSpeed = 5.0f;
    transient public String defaultPerspectiveCamera;
    transient public String defaultOrthographicCamera;
    public final Map<String, AbstractCamera> cameras = new HashMap<>();
    transient public AbstractCamera currentCamera = null;


    @Override
    public JsonElement serializeData() {
        JsonElement jsonTree = GSON.toJsonTree(this);
        JsonObject obj = jsonTree.getAsJsonObject();
        obj.addProperty("currentCamera", currentCamera.getId());
        JsonArray cameras = new JsonArray();
        obj.add("cameras", cameras);

        for (var camera : this.cameras.values()) {
            JsonObject serialized = GSON.toJsonTree(camera).getAsJsonObject();
            serialized.addProperty("isOrthographic", camera instanceof OrthographicCamera);
            cameras.add(serialized);
        }
        return obj;
    }

    @Override
    protected void parseInternal(JsonElement data) {
        JsonObject json = GSON.fromJson(data, JsonObject.class);
        pitch = json.get("pitch").getAsFloat();
        yaw = json.get("yaw").getAsFloat();
        sensitivity = json.get("sensitivity").getAsFloat();
        firstMouseMove = json.get("firstMouseMove").getAsBoolean();
        movementSpeed = json.get("movementSpeed").getAsFloat();
        json.get("cameras").getAsJsonArray().forEach(e -> {
            JsonObject obj = e.getAsJsonObject();
            AbstractCamera instance;
            if (obj.get("isOrthographic").getAsBoolean()) {
                instance = GSON.fromJson(e, OrthographicCamera.class);
            } else {
                instance = GSON.fromJson(e, PerspectiveCamera.class);
            }

            cameras.put(instance.getId(), instance);
        });
    }
}
