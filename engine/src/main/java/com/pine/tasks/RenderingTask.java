package com.pine.tasks;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.inspection.Color;
import com.pine.repository.AtmosphereRepository;
import com.pine.repository.CameraRepository;
import com.pine.repository.ClockRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.core.CoreBufferRepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.terrain.TerrainChunk;
import com.pine.repository.terrain.TerrainRepository;
import com.pine.service.grid.WorldService;
import com.pine.service.rendering.LightService;
import com.pine.service.rendering.RenderingRequestService;
import com.pine.service.rendering.TransformationService;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.ref.EnvironmentMapResourceRef;
import org.joml.Vector2f;
import org.joml.Vector3f;


@PBean
public class RenderingTask extends AbstractTask {

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public WorldService worldService;

    @PInject
    public TransformationService transformationService;

    @PInject
    public RenderingRequestService renderingRequestService;

    @PInject
    public StreamingService streamingService;

    @PInject
    public LightService lightService;

    @PInject
    public CoreBufferRepository bufferRepository;

    @PInject
    public ClockRepository clockRepository;

    @PInject
    public AtmosphereRepository atmosphere;

    @PInject
    public WorldRepository world;

    @PInject
    public TerrainRepository terrainRepository;

    @Override
    protected void tickInternal() {
        if (renderingRepository.infoUpdated) {
            return;
        }
        startTracking();
        try {
            lightService.packageLights();
            renderingRepository.offset = 0;
            renderingRepository.auxAddedToBufferEntities.clear();

            updateTiles();
            updateSunInformation();
            updateTerrainChunks();
            renderingRepository.infoUpdated = true;
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
        endTracking();
    }

    private void updateTerrainChunks() {
        if (terrainRepository.chunks == null) {
            return;
        }

        Vector2f dist = new Vector2f();
        Vector3f translation = new Vector3f();
        for (TerrainChunk chunk : terrainRepository.chunks) {
            var camPos = cameraRepository.currentCamera.position;
            int distance = (int) dist.set(chunk.normalizedX, chunk.normalizedZ)
                    .sub((float) Math.floor(camPos.x / terrainRepository.quads), (float) Math.floor(camPos.z / terrainRepository.quads))
                    .length();

            int divider = 1;
            if (distance >= 2) {
                divider = 2;
            }

            if (distance >= 3) {
                divider = 4;
            }

            if (distance >= 4) {
                divider = 8;
            }

            float tiles = (float) terrainRepository.quads / divider;
            int triangles = (int) (tiles * tiles * 6);
            chunk.setDivider(divider);
            chunk.setTriangles(triangles);
            chunk.setTiles(tiles);
            translation.set(chunk.locationX, 0, chunk.locationZ);

            chunk.setCulled(!cameraRepository.frustum.isSphereInsideFrustum(translation, terrainRepository.quads * 2));
        }
    }

    private void updateTiles() {
        int probeIndex = 0;

        for (var tile : worldService.getLoadedTiles()) {
            if (tile != null) {
                for (var entity : tile.getEntities()) {
                    var mesh = world.bagMeshComponent.get(entity);
                    if (mesh != null) {
                        var t = world.bagTransformationComponent.get(mesh.getEntityId());
                        if (t != null) {
                            mesh.distanceFromCamera = transformationService.getDistanceFromCamera(t.translation);
                            renderingRequestService.prepare(mesh, t);
                        }
                    }
                    var probe = world.bagEnvironmentProbeComponent.get(entity);
                    if (probe != null && probeIndex < 3) {
                        renderingRepository.environmentMaps[probeIndex] = (EnvironmentMapResourceRef) streamingService.streamIn(probe.getEntityId(), StreamableResourceType.ENVIRONMENT_MAP);
                        probeIndex++;
                    }
                }
            }
        }
    }

    private void updateSunInformation() {
        if (atmosphere.incrementTime) {
            atmosphere.elapsedTime += .0005f * atmosphere.elapsedTimeSpeed;
        }
        Vector3f sunLightDirection = new Vector3f((float) Math.sin(atmosphere.elapsedTime), (float) Math.cos(atmosphere.elapsedTime), 0).mul(atmosphere.sunDistance);
        Vector3f sunLightColor = computeSunlightColor(sunLightDirection);

        bufferRepository.globalDataBuffer.put(87, atmosphere.elapsedTime);

        bufferRepository.globalDataBuffer.put(88, sunLightDirection.x);
        bufferRepository.globalDataBuffer.put(89, sunLightDirection.y);
        bufferRepository.globalDataBuffer.put(90, sunLightDirection.z);
        bufferRepository.globalDataBuffer.put(91, clockRepository.totalTime - clockRepository.start);

        bufferRepository.globalDataBuffer.put(92, sunLightColor.x);
        bufferRepository.globalDataBuffer.put(93, sunLightColor.y);
        bufferRepository.globalDataBuffer.put(94, sunLightColor.z);
    }

    private Vector3f computeSunlightColor(Vector3f sunDirection) {
        return calculateSunColor(sunDirection.y / atmosphere.sunDistance, atmosphere.nightColor, atmosphere.dawnColor, atmosphere.middayColor);
    }

    public static Vector3f calculateSunColor(double elevation, Vector3f nightColor, Vector3f dawnColor, Vector3f middayColor) {
        if (elevation <= -0.1) {
            return nightColor;
        } else if (elevation <= 0.0) {
            float t = (float) ((elevation + 0.1) / 0.1);
            return blendColors(nightColor, dawnColor, t);
        } else if (elevation <= 0.5) {
            float t = (float) (elevation / 0.5);
            return blendColors(dawnColor, middayColor, t);
        } else {
            // Full daylight
            return middayColor;
        }
    }

    private static Vector3f blendColors(Vector3f c1, Vector3f c2, float t) {
        return new Vector3f(
                (c1.x * (1 - t) + c2.x * t),
                (c1.y * (1 - t) + c2.y * t),
                (c1.z * (1 - t) + c2.z * t)
        );
    }

    @Override
    public String getTitle() {
        return "Rendering logic";
    }
}
