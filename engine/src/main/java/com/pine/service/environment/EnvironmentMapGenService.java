package com.pine.service.environment;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.CameraRepository;
import com.pine.repository.EngineSettingsRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.camera.Camera;
import com.pine.service.importer.ImporterService;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.streaming.impl.CubeMapFace;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import static com.pine.service.environment.CubeMapWriteUtil.saveCubeMapToDisk;

@PBean
public class EnvironmentMapGenService implements Loggable {
    @PInject
    public WorldRepository worldRepository;

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

    @PInject
    public CameraRepository cameraRepository;


    public void bake() {
        getLogger().warn("Starting probe baking");
        boolean previous = engineSettingsRepository.disableCullingGlobally;
        engineSettingsRepository.disableCullingGlobally = true;
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
        engineSettingsRepository.disableCullingGlobally = previous;
    }

    private void capture(String resourceId, Vector3f cameraPosition) {
        getLogger().warn("Baking probe {} at position X{} Y{} Z{}", resourceId, cameraPosition.x, cameraPosition.y, cameraPosition.z);
        int baseResolution = engineSettingsRepository.probeCaptureResolution;
        var fbo = new FrameBufferObject(baseResolution, baseResolution);
        engine.setTargetFBO(fbo);
        for (int i = 0; i < CubeMapFace.values().length; i++) {
            generate(i, resourceId, cameraPosition);
        }
        fbo.dispose();
    }

    private void generate(int index, String resourceId, Vector3f cameraPosition) {
        var face = CubeMapFace.values()[index];
        var camera = new Camera();
        camera.aspectRatio = 1;
        camera.fov = (float) Math.toRadians(90);
        camera.position.set(cameraPosition);
        camera.zNear = .1f;
        camera.zFar = 1000f;
        camera.pitch = face.pitch;
        camera.yaw = face.yaw;

        cameraRepository.setCurrentCamera(camera);
        engine.render();

        getLogger().warn("Writing to disk {}", resourceId);
        saveCubeMapToDisk(engine.getTargetFBO().getMainSampler(), engine.getTargetFBO().width, importerService.getPathToFile(resourceId, StreamableResourceType.ENVIRONMENT_MAP));
    }

}
