package com.pine.service.environment;

import com.pine.Engine;
import com.pine.component.AbstractComponent;
import com.pine.component.ComponentType;
import com.pine.component.EnvironmentProbeComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.EngineSettingsRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.importer.ImporterService;
import com.pine.service.resource.ShaderService;
import com.pine.service.streaming.impl.CubeMapFace;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import java.util.Collection;

import static com.pine.service.environment.CubeMapWriteUtil.saveCubeMapToDisk;

@PBean
public class EnvironmentMapGenService implements Loggable {
    @PInject
    public WorldRepository worldRepository;

    @PInject
    public EnvironmentMapGenPass environmentMapGenPass;

    @PInject
    public ShaderService shaderService;

    @PInject
    public EngineSettingsRepository engineSettingsRepository;

    @PInject
    public ImporterService importerService;

    @PInject
    public Engine engine;

    @PInject
    public StreamingRepository streamingRepository;

    public boolean isBaked = true;

    public void bake() {
        getLogger().warn("Starting probe baking");
        // TODO - DELETE PREVIOUSLY GENERATED PROBES
        // TODO - KEEP TRACK OF GENERATED PROBES

        for (var probe : worldRepository.bagEnvironmentProbeComponent.values()) {
            capture(probe.getEntityId(), worldRepository.bagTransformationComponent.get(probe.getEntityId()).translation);
            var probeOld = streamingRepository.loadedResources.get(probe.getEntityId());
            if (probeOld != null) {
                probeOld.dispose();
            }
            streamingRepository.scheduleToLoad.remove(probe.getEntityId());
            streamingRepository.toLoadResources.remove(probe.getEntityId());
            streamingRepository.discardedResources.remove(probe.getEntityId());
        }
        isBaked = true;
    }

    private void capture(String resourceId, Vector3f cameraPosition) {
        getLogger().warn("Baking probe {} at position X{} Y{} Z{}", resourceId, cameraPosition.x, cameraPosition.y, cameraPosition.z);
        int baseResolution = engineSettingsRepository.probeCaptureResolution;
        int[] ids = CubeMapGenerator.generateFramebufferAndCubeMapTexture(baseResolution);
        int framebufferId = ids[0];
        int cubeMapTextureId = ids[1];

        generate(cameraPosition, framebufferId, baseResolution, cubeMapTextureId, resourceId);

        GL46.glDeleteBuffers(framebufferId);
        GL46.glDeleteTextures(cubeMapTextureId);
    }

    private void generate(Vector3f cameraPosition, int framebufferId, int baseResolution, int cubeMapTextureId, String resourceId) {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, framebufferId);
        for (int i = 0; i < CubeMapFace.values().length; i++) {
            GL46.glViewport(0, 0, baseResolution, baseResolution);
            GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, GL46.GL_COLOR_ATTACHMENT0, CubeMapFace.values()[i].getGlFace(), cubeMapTextureId, 0);

            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
            getLogger().warn("Rendering face {} for probe {}", i, resourceId);

            environmentMapGenPass.renderFace(CubeMapFace.values()[i], cameraPosition);
        }
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, GL46.GL_NONE);
        getLogger().warn("Writing to disk {}", resourceId);
        saveCubeMapToDisk(cubeMapTextureId, baseResolution, importerService.getPathToFile(resourceId, StreamableResourceType.ENVIRONMENT_MAP));
    }

}
