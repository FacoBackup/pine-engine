package com.pine.engine.core.service.loader;

import com.pine.engine.Engine;
import com.pine.engine.core.ClockRepository;
import com.pine.engine.core.service.loader.impl.info.MeshLoaderExtraInfo;
import com.pine.engine.core.service.loader.impl.response.MeshLoaderResponse;
import com.pine.engine.core.service.resource.ResourceService;
import com.pine.engine.core.service.resource.resource.AbstractResource;
import com.pine.engine.core.service.SystemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

import static com.pine.engine.core.service.resource.ResourceService.MAX_TIMEOUT;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ResourceLoaderServiceTest {
    private final Engine engine = Mockito.mock(Engine.class);

    @Test
    void load() {

        ClockRepository clock = new ClockRepository();
        clock.totalTime = MAX_TIMEOUT;
        Mockito.doReturn(clock).when(engine).getClock();

        ResourceLoaderService loader = new ResourceLoaderService(engine);

        ResourceService resourceServiceMock = Mockito.mock(ResourceService.class);
        Mockito.doReturn(resourceServiceMock).when(engine).getResourcesService();

        SystemService systemService = Mockito.spy(new SystemService());
        Mockito.doReturn(systemService).when(engine).getSystemsService();

        AbstractResource resourceMock = Mockito.mock(AbstractResource.class);
        Mockito.doReturn(resourceMock).when(resourceServiceMock).addResource(Mockito.any());
        Mockito.doReturn("id").when(resourceMock).getId();

        AbstractLoaderResponse load = loader.load("something.nothing", false, null);
        Assertions.assertFalse(load.isLoaded());

        load = loader.load("plane.glb", true, new MeshLoaderExtraInfo().setInstantiateHierarchy(true));

        Assertions.assertTrue(load.isLoaded());
        Assertions.assertEquals("id", ((MeshLoaderResponse) load).getMeshes().getFirst().id());



    }
}