package com.pine.service.rendering;

import com.pine.component.Entity;
import com.pine.component.MeshComponent;
import com.pine.service.streaming.mesh.MeshStreamableResource;
import com.pine.service.streaming.StreamingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RenderingRequestServiceTest {
    RenderingRequestService service = Mockito.spy(new RenderingRequestService());

    @Test
    void shouldSelectLod() {
        service.streamingService = Mockito.mock(StreamingService.class);

        var comp = new MeshComponent(new Entity());
        comp.lod4 = new MeshStreamableResource("e", "e");
        comp.distanceFromCamera = 50;
        Assertions.assertNull(service.selectLOD(comp));

        comp.lod4.loaded = true;
        Assertions.assertEquals(comp.lod4, service.selectLOD(comp));

        comp.lod0 = new MeshStreamableResource("e", "e");
        Assertions.assertEquals(comp.lod4, service.selectLOD(comp));

        comp.lod0.loaded = true;
        Assertions.assertEquals(comp.lod0, service.selectLOD(comp));

        comp.lod4 = null;
        comp.distanceFromCamera = 500;
        Assertions.assertEquals(comp.lod0, service.selectLOD(comp));
    }
}