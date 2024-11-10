package com.pine.service.environment;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.CameraRepository;
import com.pine.repository.EngineSettingsRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.camera.Camera;
import com.pine.service.grid.HashGridService;
import com.pine.service.importer.ImporterService;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.streaming.impl.CubeMapFace;
import com.pine.service.streaming.impl.TextureService;
import org.joml.Vector3f;


@PBean
public class EnvironmentMapGenService implements Loggable {
    @PInject
    public HashGridService hashGridService;

    @PInject
    public ShaderService shaderService;

    @PInject
    public EngineSettingsRepository engineSettingsRepository;

    @PInject
    public ImporterService importerService;

    @PInject
    public Engine engine;

    @PInject
    public TextureService textureService;

    @PInject
    public StreamingRepository streamingRepository;

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public RuntimeRepository runtimeRepository;


    public void bake() {
        engineSettingsRepository.isBakingEnvironmentMaps = true;
        getLogger().warn("Starting probe baking");
        boolean previous = engineSettingsRepository.disableCullingGlobally;
        engineSettingsRepository.disableCullingGlobally = true;

        for(var tile : hashGridService.getTiles().values()){
            if(tile != null){
                for (var probe : tile.getWorld().bagEnvironmentProbeComponent.values()) {
                    capture(probe.getEntityId(), tile.getWorld().bagTransformationComponent.get(probe.getEntityId()).translation);
                    var probeOld = streamingRepository.streamed.get(probe.getEntityId());
                    if (probeOld != null) {
                        probeOld.dispose();
                    }
                    streamingRepository.toStreamIn.remove(probe.getEntityId());
                    streamingRepository.streamData.remove(probe.getEntityId());
                    streamingRepository.discardedResources.remove(probe.getEntityId());
                }
            }
        }

        engineSettingsRepository.disableCullingGlobally = previous;
        engineSettingsRepository.isBakingEnvironmentMaps = false;
    }

    private void capture(String resourceId, Vector3f cameraPosition) {
        getLogger().warn("Baking probe {} at position X{} Y{} Z{}", resourceId, cameraPosition.x, cameraPosition.y, cameraPosition.z);
        int baseResolution = engineSettingsRepository.probeCaptureResolution;
        var fbo = new FrameBufferObject(baseResolution, baseResolution).addSampler();
        engine.setTargetFBO(fbo);
        for (int i = 0; i < CubeMapFace.values().length; i++) {
            generate(i, resourceId, cameraPosition);
        }
        fbo.dispose();
    }

    private void generate(int index, String resourceId, Vector3f cameraPosition) {
        var face = CubeMapFace.values()[index];
        var camera = new Camera();
        camera.fov = (float) Math.toRadians(90);
        camera.position.set(cameraPosition);
        camera.zNear = .1f;
        camera.zFar = 1000f;
        camera.pitch = face.pitch;
        camera.yaw = face.yaw;

        runtimeRepository.viewportH = engine.getTargetFBO().height;
        runtimeRepository.viewportW = engine.getTargetFBO().width;

        cameraRepository.setCurrentCamera(camera);
        engine.render();

        String basePath = importerService.getPathToFile(resourceId, StreamableResourceType.ENVIRONMENT_MAP);
        getLogger().warn("Writing to disk {}", resourceId);
        textureService.writeTexture(getPathToFile(basePath, face), engine.getTargetFBO().width, engine.getTargetFBO().height, engine.getTargetFBO().getMainSampler());
    }

    public static String getPathToFile(String basePath, CubeMapFace face) {
        String type = StreamableResourceType.ENVIRONMENT_MAP.name();
        return basePath.replace("." + type, "-" + face.name() + type + ".png");
    }
}
