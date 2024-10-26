package com.pine.service.environment;

import com.pine.component.ComponentType;
import com.pine.component.MeshComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.*;
import com.pine.repository.core.*;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.module.Initializable;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.impl.MaterialService;
import com.pine.service.streaming.impl.MeshService;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.MeshResourceRef;
import com.pine.service.system.SystemService;
import com.pine.service.system.impl.AtmospherePass;
import org.joml.Matrix4f;

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

    @Override
    public void onInitialize() {
        atmospherePass = (AtmospherePass) systemService.getSystems().stream().filter(a -> a instanceof AtmospherePass).findFirst().orElse(null);
        fallbackMaterial = shaderRepository.environmentMap.addUniformDeclaration("fallbackMaterial", GLSLType.BOOL);
        viewProjection = shaderRepository.environmentMap.addUniformDeclaration("viewProjection", GLSLType.MAT_4);
        model = shaderRepository.environmentMap.addUniformDeclaration("model", GLSLType.MAT_4);
    }

    public void renderFace(Matrix4f viewMatrix, Matrix4f invView, Matrix4f projection, Matrix4f invProjection) {
        if (!initialized) {
            onInitialize();
            initialized = true;
        }

        if (atmosphereSettingsRepository.enabled) {
            atmospherePass.renderToCubeMap(invView, invProjection);
        }

        var viewProjection = new Matrix4f();
        viewProjection.set(projection).mul(viewMatrix);
        shaderService.bind(shaderRepository.environmentMap);
        shaderService.bindMat4(viewProjection, this.viewProjection);

        for (var comp : worldRepository.components.get(ComponentType.MESH)) {
            var meshComp = (MeshComponent) comp;
            if (meshComp.lod0 != null) {
                var mesh = (MeshResourceRef) streamingService.streamSync(meshComp.lod0, StreamableResourceType.MESH);
                var material = (MaterialResourceRef) streamingService.streamSync(meshComp.material, StreamableResourceType.MATERIAL);
                if (mesh != null) {
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

                    meshService.bind(mesh);
                    if (meshComp.isInstancedRendering) {
                        meshComp.instances.forEach(i -> {
                            shaderService.bindMat4(i.globalMatrix, model);
                            meshService.draw();
                        });
                    } else {
                        shaderService.bindMat4(meshComp.entity.transformation.globalMatrix, model);
                        meshService.draw();
                    }
                }
            }
        }
    }
}
