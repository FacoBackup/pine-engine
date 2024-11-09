package com.pine.tasks;

import com.pine.component.MeshComponent;
import com.pine.component.TransformationComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.*;
import com.pine.repository.core.CoreBufferRepository;
import com.pine.repository.rendering.LightUtil;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.environment.EnvironmentMapGenService;
import com.pine.service.rendering.LightService;
import com.pine.service.rendering.RenderingRequestService;
import com.pine.service.rendering.TransformationService;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.ref.EnvironmentMapResourceRef;
import com.pine.service.streaming.ref.VoxelChunkResourceRef;
import org.joml.Vector3f;


@PBean
public class RenderingTask extends AbstractTask {

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public WorldRepository worldRepository;

    @PInject
    public TransformationService transformationService;

    @PInject
    public RenderingRequestService renderingRequestService;

    @PInject
    public VoxelRepository voxelRepository;

    @PInject
    public StreamingService streamingService;

    @PInject
    public EnvironmentMapGenService environmentMapGenService;

    @PInject
    public LightService lightService;

    @PInject
    public CoreBufferRepository bufferRepository;

    @PInject
    public ClockRepository clockRepository;

    @PInject
    public AtmosphereRepository atmosphere;

    private int renderIndex = 0;
    private float elapsedTime = .5f;

    @Override
    protected void tickInternal() {
        if (renderingRepository.infoUpdated) {
            return;
        }
        startTracking();
        try {
            defineVoxelGrid();
            defineProbes();
            lightService.packageLights();
            renderIndex = 0;
            renderingRepository.offset = 0;
            renderingRepository.auxAddedToBufferEntities.clear();
            renderingRepository.pendingTransformationsInternal = 0;
            renderingRepository.newRequests.clear();

            for (var mesh : worldRepository.bagMeshComponent.values()) {
                var t = worldRepository.bagTransformationComponent.get(mesh.getEntityId());
                if (t != null) {
                    updateMeshData(mesh, t);
                }
            }


            updateSunInformation();

            renderingRepository.infoUpdated = true;
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
        endTracking();
    }

    private void updateSunInformation() {
        elapsedTime += .0005f * atmosphere.elapsedTimeSpeed;
        Vector3f sunLightDirection = new Vector3f((float) Math.sin(elapsedTime), (float) Math.cos(elapsedTime), 0).mul(atmosphere.sunDistance);
        Vector3f sunLightColor = LightUtil.computeSunlightColor(sunLightDirection, cameraRepository.currentCamera.position);

        bufferRepository.globalDataBuffer.put(87, elapsedTime);

        bufferRepository.globalDataBuffer.put(88, sunLightDirection.x);
        bufferRepository.globalDataBuffer.put(89, sunLightDirection.y);
        bufferRepository.globalDataBuffer.put(90, sunLightDirection.z);
        bufferRepository.globalDataBuffer.put(91, 0);

        bufferRepository.globalDataBuffer.put(92, sunLightColor.x);
        bufferRepository.globalDataBuffer.put(93, sunLightColor.y);
        bufferRepository.globalDataBuffer.put(94, sunLightColor.z);
    }

    private void defineProbes() {
        var probes = worldRepository.bagEnvironmentProbeComponent.values();
        if (environmentMapGenService.isBaked) {
            int i = 0;
            for (var probe : probes) {
                if (i == 3) {
                    break;
                }
                renderingRepository.environmentMaps[i] = (EnvironmentMapResourceRef) streamingService.stream(probe.getEntityId(), StreamableResourceType.ENVIRONMENT_MAP);
                i++;
            }
        }
    }

    private void defineVoxelGrid() {
        if (voxelRepository.grid != null) {
            int filledWithContent = 0;
            int filled = 0;

            // TODO - SORT BY DISTANCE
            for (var chunk : voxelRepository.grid.chunks) {
                boolean culled = false;// transformationService.isCulled(chunk.getCenter(), 100, new Vector3f((float) chunk.getSize()));
                if (culled || filled > 3) {
                    continue;
                }
                var chunkStream = (VoxelChunkResourceRef) streamingService.stream(chunk.getId(), StreamableResourceType.VOXEL_CHUNK);
                renderingRepository.newVoxelChunks[filled] = chunkStream;
                if (chunkStream != null) {
                    filledWithContent++;
                    chunkStream.size = chunk.getSize();
                    chunkStream.center = chunk.getCenter();
                    chunkStream.depth = chunk.getDepth();
                }
                filled++;
            }
            renderingRepository.voxelChunksFilled = filledWithContent;
        }
    }

    private void updateMeshData(MeshComponent meshComponent, TransformationComponent transformation) {
        meshComponent.distanceFromCamera = transformationService.getDistanceFromCamera(transformation.translation);
        if (meshComponent.isInstancedRendering) {
            RenderingRequest request = renderingRequestService.prepareInstanced(meshComponent, transformation);
            if (request != null) {
                request.renderIndex = renderIndex;
                renderIndex += request.transformationComponents.size() + 1;
                renderingRepository.newRequests.add(request);
            }
        } else {
            RenderingRequest request = renderingRequestService.prepareNormal(meshComponent, transformation);
            if (request != null) {
                request.renderIndex = renderIndex;
                renderIndex++;
                renderingRepository.newRequests.add(request);
            }
        }
    }

    @Override
    public String getTitle() {
        return "Rendering logic";
    }
}
