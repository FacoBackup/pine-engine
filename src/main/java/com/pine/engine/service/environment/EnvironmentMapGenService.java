package com.pine.engine.service.environment;

import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.common.messaging.Loggable;
import com.pine.engine.Engine;
import com.pine.engine.repository.CameraRepository;
import com.pine.engine.repository.EngineRepository;
import com.pine.engine.repository.RuntimeRepository;
import com.pine.engine.repository.WorldRepository;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.repository.streaming.StreamingRepository;
import com.pine.engine.service.camera.Camera;
import com.pine.engine.service.importer.ImporterService;
import com.pine.engine.service.resource.fbo.FBOCreationData;
import com.pine.engine.service.resource.fbo.FBOService;
import com.pine.engine.service.resource.shader.ShaderService;
import com.pine.engine.service.streaming.impl.CubeMapFace;
import com.pine.engine.service.streaming.impl.TextureService;
import com.pine.engine.service.world.WorldService;
import org.joml.Vector3f;


@PBean
public class EnvironmentMapGenService implements Loggable {
    @PInject
    public WorldService worldService;

    @PInject
    public ShaderService shaderService;

    @PInject
    public EngineRepository engineRepository;

    @PInject
    public FBOService fboService;

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

    @PInject
    public WorldRepository world;


    public void bake() {
        engineRepository.isBakingEnvironmentMaps = true;
        getLogger().warn("Starting probe baking");
        boolean previous = engineRepository.disableCullingGlobally;
        engineRepository.disableCullingGlobally = true;

        for (var tile : worldService.getTiles().values()) {
            for (var entity : tile.getEntities()) {
                var probe = world.bagEnvironmentProbeComponent.get(entity);
                if (probe == null) {
                    continue;
                }
                capture(probe.getEntityId(), world.bagTransformationComponent.get(probe.getEntityId()).translation);
                var probeOld = streamingRepository.streamed.get(probe.getEntityId());
                if (probeOld != null) {
                    probeOld.dispose();
                }
                streamingRepository.toStreamIn.remove(probe.getEntityId());
                streamingRepository.streamData.remove(probe.getEntityId());
                streamingRepository.discardedResources.remove(probe.getEntityId());
            }
        }

        engineRepository.disableCullingGlobally = previous;
        engineRepository.isBakingEnvironmentMaps = false;
    }

    private void capture(String resourceId, Vector3f cameraPosition) {
        getLogger().warn("Baking probe {} at position X{} Y{} Z{}", resourceId, cameraPosition.x, cameraPosition.y, cameraPosition.z);
        int baseResolution = engineRepository.probeCaptureResolution;
        var fbo = fboService.create(new FBOCreationData(baseResolution, baseResolution, false).addSampler("Environment map"));
        engine.setTargetFBO(fbo);
        for (int i = 0; i < CubeMapFace.values().length; i++) {
            generate(i, resourceId, cameraPosition);
        }
        fboService.dispose(fbo);
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
