package com.pine.service.environment;

import com.pine.Engine;
import com.pine.component.AbstractComponent;
import com.pine.component.ComponentType;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.EngineSettingsRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.ImporterService;
import com.pine.service.streaming.LevelOfDetail;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import java.util.List;
import java.util.UUID;

import static com.pine.service.environment.CubeMapWriteUtil.saveCubeMapToDisk;

@PBean
public class EnvironmentMapGenService implements Loggable {
    private static final float FOV_Y = (float) (Math.PI / 2f);
    private static final float ASPECT_RATIO = 1f;
    private static final float Z_NEAR = .1f;
    private static final float Z_FAR = 10000f;

    private static final int[] CUBEMAP_FACES = {
            GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
            GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
            GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
            GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
            GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
            GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
    };

    private static final Vector3f[] TARGETS = {
            new Vector3f(1.0f, 0.0f, 0.0f),   // +X
            new Vector3f(-1.0f, 0.0f, 0.0f),  // -X
            new Vector3f(0.0f, 1.0f, 0.0f),   // +Y
            new Vector3f(0.0f, -1.0f, 0.0f),  // -Y
            new Vector3f(0.0f, 0.0f, 1.0f),   // +Z
            new Vector3f(0.0f, 0.0f, -1.0f)   // -Z
    };

    private static final Vector3f[] UP_VECTORS = {
            new Vector3f(0.0f, -1.0f, 0.0f),  // +X
            new Vector3f(0.0f, -1.0f, 0.0f),  // -X
            new Vector3f(0.0f, 0.0f, 1.0f),   // +Y
            new Vector3f(0.0f, 0.0f, -1.0f),  // -Y
            new Vector3f(0.0f, -1.0f, 0.0f),  // +Z
            new Vector3f(0.0f, -1.0f, 0.0f)   // -Z
    };

    @PInject
    public WorldRepository worldRepository;

    @PInject
    public EngineSettingsRepository engineSettingsRepository;

    @PInject
    public ImporterService importerService;

    @PInject
    public Engine engine;

    public void bake() {
        // TODO - DELETE PREVIOUSLY GENERATED PROBES
        // TODO - KEEP TRACK OF GENERATED PROBES

        List<AbstractComponent> probes = worldRepository.components.get(ComponentType.ENVIRONMENT_PROBE);
        for (var probe : probes) {
            capture(probe.entity.id, probe.entity.transformation.translation);
        }
    }

    private void capture(String resourceId, Vector3f cameraPosition) {
        int baseResolution = engineSettingsRepository.probeCaptureResolution;
        int[] ids = CubeMapGenerator.generateFramebufferAndCubeMapTexture(baseResolution);
        int framebufferId = ids[0];
        int cubeMapTextureId = ids[1];

        generateSpecularLOD(cameraPosition, framebufferId, baseResolution, cubeMapTextureId, resourceId);

        GL46.glDeleteBuffers(framebufferId);
        GL46.glDeleteTextures(cubeMapTextureId);
    }

    private void generateSpecularLOD(Vector3f cameraPosition, int framebufferId, int baseResolution, int cubeMapTextureId, String resourceId) {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, framebufferId);
        Matrix4f projection = new Matrix4f();
        projection.setPerspective(FOV_Y, ASPECT_RATIO, Z_NEAR, Z_FAR);
        for (int i = 0; i < 6; i++) {
            GL46.glViewport(0, 0, baseResolution, baseResolution);
            GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, GL46.GL_COLOR_ATTACHMENT0, CUBEMAP_FACES[i], cubeMapTextureId, 0);
            Matrix4f viewMatrix = createViewMatrixForFace(i, cameraPosition);
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);

            // TODO - RENDER SCENE + ATMOSPHERE IF ENABLED
        }
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
        saveCubeMapToDisk(cubeMapTextureId, baseResolution, importerService.getPathToFile(resourceId, StreamableResourceType.ENVIRONMENT_MAP), LevelOfDetail.LOD_0);
    }

    private Matrix4f createViewMatrixForFace(int faceIndex, Vector3f cameraPosition) {
        Vector3f target = TARGETS[faceIndex];
        Vector3f up = UP_VECTORS[faceIndex];
        return new Matrix4f().lookAt(cameraPosition, target, up);
    }
}
