import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pine.Engine;
import com.pine.service.system.SystemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static com.pine.service.serialization.SerializableRepository.CLASS_KEY;
import static com.pine.service.serialization.SerializableRepository.DATA_KEY;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EngineTest {
    private static String serialized;

    private Engine engine;

    @BeforeEach
    void setUpBefore() {
//        engine = new Engine();
//        int world = engine.getSystemsService().addEntity();
//        engine.getSystemsService().addComponent(world, MeshComponent.class);
//
//        int entity2 = engine.getSystemsService().addEntity();
//        engine.getSystemsService().addComponent(entity2, TransformationComponent.class);
//
//        serialized = engine.serialize().toString();
    }

    @Test
    @Order(1)
    void serializeAll() {
        testDump(2, serialized);
    }

    void testDump(int expectedSize, String dump) {
        JsonObject element = new Gson().fromJson(dump, JsonObject.class);
        assertNotNull(element);
        assertFalse(element.isEmpty());
        JsonObject serviceData = element.getAsJsonObject();
        if (Objects.equals(serviceData.get(CLASS_KEY).getAsString(), SystemService.class.getName())) {
            JsonArray entitiesArray = serviceData.get(DATA_KEY).getAsJsonArray();
            assertEquals(expectedSize, entitiesArray.size());
        }
    }

    @Test
    @Order(2)
    void parseAll() {
//        engine.getSystemsService().getWorld().delete(0);
//        engine.getSystemsService().getWorld().delete(1);
//        engine.getSystemsService().getWorld().process();
//        testDump(0, engine.serialize().toString());
//        engine.parse(serialized);
//        testDump(2, engine.serialize().toString());
    }
}