package com.pine.core.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.app.view.core.service.WindowService;
import com.pine.common.SerializationService;
import com.pine.core.components.component.MeshComponent;
import com.pine.core.components.component.TransformationComponent;
import com.pine.core.WorldRepository;
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
    WorldRepository worldRepository;

    @Autowired
    WindowService windowService;

    @BeforeAll
    static void setUp() {
        WindowService.shouldStop = true;
    }

    @BeforeEach
    void setUpBefore() {
        int entity = worldRepository.addEntity();
        worldRepository.addComponent(entity, MeshComponent.class);

        int entity2 = worldRepository.addEntity();
        worldRepository.addComponent(entity2, TransformationComponent.class);

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
            if (Objects.equals(serviceData.get(CLASS_KEY).getAsString(), WorldRepository.class.getName())) {
                JsonArray entitiesArray = serviceData.get(DATA_KEY).getAsJsonArray();
                assertEquals(expectedSize, entitiesArray.size());
            }
        }
    }

    @Test
    @Order(2)
    void parseAll() {
        worldRepository.getWorld().delete(0);
        worldRepository.getWorld().delete(1);
        worldRepository.getWorld().process();
        testDump(0, serializationService.serializeAll());
        serializationService.parseAll(serialized);
        testDump(2, serializationService.serializeAll());
    }
}