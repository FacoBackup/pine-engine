package com.pine.engine.repository.terrain;

import com.pine.common.Icons;
import com.pine.common.SerializableRepository;
import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.common.inspection.ExecutableField;
import com.pine.common.inspection.Inspectable;
import com.pine.common.inspection.InspectableField;
import com.pine.common.inspection.ListInspection;
import com.pine.engine.inspection.ResourceTypeField;
import com.pine.engine.repository.core.CoreBufferRepository;
import com.pine.engine.repository.core.CoreMeshRepository;
import com.pine.engine.repository.core.CoreShaderRepository;
import com.pine.engine.repository.rendering.RenderingMode;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.importer.ImporterService;
import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.fbo.FBOCreationData;
import com.pine.engine.service.resource.fbo.FBOService;
import com.pine.engine.service.resource.shader.ShaderService;
import com.pine.engine.service.streaming.StreamingService;
import com.pine.engine.service.streaming.impl.MeshService;
import com.pine.engine.service.streaming.impl.TextureService;
import com.pine.engine.util.ImageUtil;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@PBean
public class TerrainRepository extends Inspectable implements SerializableRepository {
    public String materialMask = UUID.randomUUID().toString();
    public String heightMapTexture = UUID.randomUUID().toString();
    public TerrainChunk[] chunks = null;

    @PInject
    public transient ImporterService importer;
    @PInject
    public transient ShaderService shaderService;
    @PInject
    public transient StreamingService streamingService;
    @PInject
    public transient FBOService fboService;
    @PInject
    public transient CoreShaderRepository coreShaderRepository;
    @PInject
    public transient CoreMeshRepository coreMeshRepository;
    @PInject
    public transient MeshService meshService;
    @PInject
    public transient TextureService textureService;

    @ExecutableField(label = "Re-Gen material mask")
    public void genMaterialMask() {
        String path = importer.getPathToFile(materialMask, StreamableResourceType.TEXTURE);
        String pathToHeightMap = importer.getPathToFile(heightMapTexture, StreamableResourceType.TEXTURE);
        var materialMask = streamingService.streamTextureSync(path);
        var heightMap = streamingService.streamTextureSync(pathToHeightMap);

        if(heightMap != null && materialMask != null) {
            FBO fbo = fboService.create(new FBOCreationData(materialMask.width, materialMask.height, false).addSampler("TESTTTTTTTTTTTTTT"));

            shaderService.bind(coreShaderRepository.terrainMaterialMaskGenShader);
            fbo.startMapping(true);

            shaderService.bindVec4(materialLayers.materialLayerA.channel, coreShaderRepository.terrainMaterialMaskGenShader.addUniformDeclaration("color1"));
            shaderService.bindVec4(materialLayers.materialLayerB.channel, coreShaderRepository.terrainMaterialMaskGenShader.addUniformDeclaration("color2"));
            shaderService.bindVec4(materialLayers.materialLayerC.channel, coreShaderRepository.terrainMaterialMaskGenShader.addUniformDeclaration("color3"));
            shaderService.bindVec4(materialLayers.materialLayerD.channel, coreShaderRepository.terrainMaterialMaskGenShader.addUniformDeclaration("color4"));

            shaderService.bindSampler2dDirect(heightMap.texture, 0);

            meshService.bind(coreMeshRepository.quadMesh);
            meshService.setRenderingMode(RenderingMode.TRIANGLES);
            GL46.glDisable(GL11.GL_DEPTH_TEST);
            GL46.glDisable(GL11.GL_BLEND);
            meshService.draw();
            shaderService.unbind();
            fbo.stop();
            GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);

            textureService.writeTexture(path, materialMask.width, materialMask.height, fbo.getMainSampler());
            fboService.dispose(fbo);
            materialMask.dispose();
            streamingService.repository.streamed.remove(this.materialMask);
        }
    }

    @ExecutableField(label = "Import data")
    public void importTerrain() {
        new Thread(() -> {
            tryDelete(materialMask);
            tryDelete(heightMapTexture);

            int width = cellsX * quads;
            int height = cellsZ * quads;

            String heightMapTextureLocal = UUID.randomUUID().toString();
            String materialMaskLocal = UUID.randomUUID().toString();

            ImageUtil.generateTexture(width, height, importer.getPathToFile(heightMapTextureLocal, StreamableResourceType.TEXTURE));
            ImageUtil.generateTexture(width, height, importer.getPathToFile(materialMaskLocal, StreamableResourceType.TEXTURE));

            if (heightMapTextureToImport != null) {
                var targetPath = importer.getPathToFile(heightMapTextureLocal, StreamableResourceType.TEXTURE);
                var originPath = importer.getPathToFile(heightMapTextureToImport, StreamableResourceType.TEXTURE);
                ImageUtil.copyInto(originPath, targetPath, 1);
            }

            var newChunks = new TerrainChunk[cellsX * cellsZ];
            int index = 0;
            for (int x = 0; x < cellsX; x++) {
                for (int z = 0; z < cellsZ; z++) {
                    float locationX = x * quads + offset.x;
                    float locationZ = z * quads + offset.y;
                    newChunks[index] = new TerrainChunk(locationX, locationZ, (float) Math.floor(locationX / quads), (float) Math.floor(locationZ / quads), x, z);
                    index++;
                }
            }
            heightMapTexture = heightMapTextureLocal;
            materialMask = materialMaskLocal;
            chunks = newChunks;
        }).start();
    }

    private void tryDelete(String id) {
        try {
            Files.delete(Paths.get(importer.getPathToFile(id, StreamableResourceType.TEXTURE)));
        } catch (Exception ignored) {
        }
    }

    @InspectableField(label = "Render terrain")
    public boolean enabled = false;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(label = "Height map to import")
    public String heightMapTextureToImport;

    @InspectableField(group = "Terrain", label = "Casts shadows")
    public boolean castsShadows = true;

    @InspectableField(group = "Terrain", label = "Cells X axis", min = 1)
    public int cellsX = 2;

    @InspectableField(group = "Terrain", label = "Cells Z axis", min = 1)
    public int cellsZ = 2;

    @InspectableField(group = "Terrain", label = "Quads per cell (X by X)", min = 1)
    public int quads = 150;

    @InspectableField(group = "Terrain", label = "Height scale")
    public float heightScale = 1;

    @InspectableField(group = "Terrain", label = "Offset")
    public Vector2f offset = new Vector2f(0);

    @InspectableField(label = "Materials")
    public MaterialLayers materialLayers = new MaterialLayers();


    @ListInspection(clazzType = FoliageInstance.class)
    @InspectableField(label = "Foliage")
    public final List<FoliageInstance> foliage = new ArrayList<>();

    @Override
    public String getTitle() {
        return "Terrain & Foliage";
    }

    @Override
    public String getIcon() {
        return Icons.terrain;
    }
}