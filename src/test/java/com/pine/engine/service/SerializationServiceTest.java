package com.pine.engine.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.app.core.service.WindowService;
import com.pine.common.serialization.SerializationService;
import com.pine.engine.components.component.MeshComponent;
import com.pine.engine.components.component.TransformationComponent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static com.pine.common.serialization.SerializableRepository.CLASS_KEY;
import static com.pine.common.serialization.SerializableRepository.DATA_KEY;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SerializationServiceTest {
    private static String serialized;

    @Autowired
    SerializationService serializationService;

    @Autowired
    WorldService worldService;

    @Autowired
    WindowService windowService;

    @BeforeAll
    static void setUp() {
        WindowService.shouldStop = true;
    }

    @BeforeEach
    void setUpBefore() {
        int entity = worldService.addEntity();
        worldService.addComponent(entity, MeshComponent.class);

        int entity2 = worldService.addEntity();
        worldService.addComponent(entity2, TransformationComponent.class);

        serialized = serializationService.serializeAll();
    }

    @Test
    @Order(1)
    void serializeAll() {
        testDump(2, serialized);
    }

    void testDump(int expectedSize, String dump) {
        JsonArray data = new Gson().fromJson(dump, JsonArray.class);
        assertNotNull(data);
        assertFalse(data.isEmpty());
        for (JsonElement element : data) {
            JsonObject serviceData = element.getAsJsonObject();
            if (Objects.equals(serviceData.get(CLASS_KEY).getAsString(), WorldService.class.getName())) {
                JsonArray entitiesArray = serviceData.get(DATA_KEY).getAsJsonArray();
                assertEquals(expectedSize, entitiesArray.size());
            }
        }
    }

    @Test
    @Order(2)
    void parseAll() {
        worldService.getWorld().delete(0);
        worldService.getWorld().delete(1);
        worldService.getWorld().process();
        testDump(0, serializationService.serializeAll());
        serializationService.parseAll(serialized);
        testDump(2, serializationService.serializeAll());
    }
}