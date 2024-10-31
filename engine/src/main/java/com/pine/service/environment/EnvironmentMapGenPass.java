package com.pine.service.environment;

import com.pine.component.ComponentType;
import com.pine.component.MeshComponent;
import com.pine.component.TransformationComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.AtmosphereSettingsRepository;
import com.pine.repository.CameraRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.core.CoreFBORepository;
import com.pine.repository.core.CoreShaderRepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.module.Initializable;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.impl.CubeMapFace;
import com.pine.service.streaming.impl.MaterialService;
import com.pine.service.streaming.impl.MeshService;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.MeshResourceRef;
import com.pine.service.system.SystemService;
import com.pine.service.system.impl.AtmospherePass;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@PBean
public class EnvironmentMapGenPass implements Initializable {
    @PInject
    public MeshService meshService;
    @PInject
    public CameraRepository cameraRepository;
    @PInject
    public RenderingRepository renderingRepository;
    @PInject
    public ShaderService shaderService;
    @PInject
    public CoreShaderRepository shaderRepository;
    @PInject
    public MaterialService materialService;
    @PInject
    public StreamingService streamingService;
    @PInject
    public CoreFBORepository fboRepository;
    @PInject
    public AtmosphereSettingsRepository atmosphereSettingsRepository;
    @PInject
    public WorldRepository worldRepository;
    @PInject
    public SystemService systemService;

    private boolean initialized;
    private AtmospherePass atmospherePass;

    private UniformDTO fallbackMaterial;
    private UniformDTO viewProjection;
    private UniformDTO model;
    private UniformDTO albedoColor;
    private UniformDTO roughnessMetallic;
    private UniformDTO useAlbedoRoughnessMetallicAO;
    private UniformDTO useNormalTexture;

    @Override
    public void onInitialize() {
        atmospherePass = (AtmospherePass) systemService.getSystems().stream().filter(a -> a instanceof AtmospherePass).findFirst().orElse(null);
        fallbackMaterial = shaderRepository.environmentMap.addUniformDeclaration("fallbackMaterial");
        viewProjection = shaderRepository.environmentMap.addUniformDeclaration("viewProjection");
        albedoColor = shaderRepository.environmentMap.addUniformDeclaration("albedoColor");
        roughnessMetallic = shaderRepository.environmentMap.addUniformDeclaration("roughnessMetallic");
        useAlbedoRoughnessMetallicAO = shaderRepository.environmentMap.addUniformDeclaration("useAlbedoRoughnessMetallicAO");
        useNormalTexture = shaderRepository.environmentMap.addUniformDeclaration("useNormalTexture");
        model = shaderRepository.environmentMap.addUniformDeclaration("model");
    }

    public void renderFace(CubeMapFace face, Vector3f cameraPosition) {
        if (!initialized) {
            onInitialize();
            initialized = true;
        }

        if (atmosphereSettingsRepository.enabled) {
            Matrix4f centeredViewMatrixInv = CubeMapFace.createViewMatrixForFace(face, new Vector3f(0));
            centeredViewMatrixInv.invert(centeredViewMatrixInv);
            atmospherePass.renderToCubeMap(centeredViewMatrixInv, CubeMapFace.invProjection);
        }


        Matrix4f viewMatrix = CubeMapFace.createViewMatrixForFace(face, cameraPosition);
        var viewProjection = new Matrix4f();
        viewProjection.set(CubeMapFace.projection).mul(viewMatrix);
        shaderService.bind(shaderRepository.environmentMap);
        shaderService.bindMat4(viewProjection, this.viewProjection);

        for (var meshComp : worldRepository.bagMeshComponent.values()) {
            if (meshComp.lod0 != null && meshComp.contributeToProbes) {
                var mesh = (MeshResourceRef) streamingService.streamSync(meshComp.lod0, StreamableResourceType.MESH);
                var material = (MaterialResourceRef) streamingService.streamSync(meshComp.material, StreamableResourceType.MATERIAL);
                if (mesh != null) {
                    bindMaterial(material);
                    draw(mesh, meshComp);
                }
            }
        }
    }

    private void draw(MeshResourceRef mesh, MeshComponent meshComp) {
        meshService.bind(mesh);
        if (meshComp.isInstancedRendering) {
            for (TransformationComponent i : meshComp.instances) {
                shaderService.bindMat4(i.globalMatrix, model);
                meshService.draw();
            }
        } else {
            shaderService.bindMat4(worldRepository.bagTransformationComponent.get(meshComp.getEntityId()).globalMatrix, model);
            meshService.draw();
        }
    }

    private void bindMaterial(MaterialResourceRef material) {
        if (material != null) {
            shaderService.bindBoolean(false, fallbackMaterial);
            material.anisotropicRotationUniform = UniformDTO.EMPTY;
            material.anisotropyUniform = UniformDTO.EMPTY;
            material.clearCoatUniform = UniformDTO.EMPTY;
            material.sheenUniform = UniformDTO.EMPTY;
            material.sheenTintUniform = UniformDTO.EMPTY;
            material.renderingModeUniform = UniformDTO.EMPTY;
            material.ssrEnabledUniform = UniformDTO.EMPTY;
            material.parallaxHeightScaleUniform = UniformDTO.EMPTY;
            material.parallaxLayersUniform = UniformDTO.EMPTY;
            material.useParallaxUniform = UniformDTO.EMPTY;

            material.albedoColorLocation = albedoColor;
            material.roughnessMetallicLocation = roughnessMetallic;
            material.useAlbedoRoughnessMetallicAO = useAlbedoRoughnessMetallicAO;
            material.useNormalTexture = useNormalTexture;

            material.albedoLocation = 0;
            material.roughnessLocation = 1;
            material.metallicLocation = 2;
            material.aoLocation = 3;
            material.normalLocation = 4;
            material.heightMapLocation = 5;

            materialService.bindMaterial(material);
        } else {
            shaderService.bindBoolean(true, fallbackMaterial);
        }
    }
}
