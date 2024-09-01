package com.pine.engine.service;

import com.pine.common.resource.*;
import com.pine.engine.service.primitives.audio.Audio;
import com.pine.engine.service.primitives.audio.AudioDTO;
import com.pine.engine.service.primitives.material.Material;
import com.pine.engine.service.primitives.material.MaterialDTO;
import com.pine.engine.service.primitives.mesh.Mesh;
import com.pine.engine.service.primitives.mesh.MeshDTO;
import com.pine.engine.service.primitives.mesh.MeshRuntimeData;
import com.pine.engine.service.primitives.shader.Shader;
import com.pine.engine.service.primitives.shader.ShaderCreationDTO;
import com.pine.engine.service.primitives.shader.ShaderRuntimeData;
import com.pine.engine.service.primitives.texture.TextureCreationDTO;
import com.pine.engine.service.primitives.ubo.UBO;
import com.pine.engine.service.primitives.ubo.UBOCreationData;
import com.pine.engine.service.primitives.ubo.UBORuntimeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pine.common.resource.ResourceRepository.MAX_TIMEOUT;

@Service
public class ResourceService implements IResourceService {

    @Autowired
    private ResourceRepository repository;

    @Autowired
    private AudioService audioService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MeshService meshService;

    @Autowired
    private ShaderService shaderService;

    @Autowired
    private TextureService textureService;

    @Autowired
    private UBOService uboService;

    @Override
    public <C extends IResourceCreationData> IResource addResource(C data) {
        IResource instance = null;
        switch (data.getResourceType()) {
            case UBO: {
                instance = uboService.add((UBOCreationData) data);
                break;
            }
            case MESH: {
                instance = meshService.add((MeshDTO) data);
                break;
            }
            case MATERIAL: {
                instance = materialService.add((MaterialDTO) data);
                break;
            }
            case SHADER: {
                instance = shaderService.add((ShaderCreationDTO) data);
                break;
            }
            case AUDIO: {
                instance = audioService.add((AudioDTO) data);
                break;
            }
            case TEXTURE: {
                instance = textureService.add((TextureCreationDTO) data);
                break;
            }
        }
        if (instance == null) {
            getLogger().warn("Resource could not be initialized correctly: {}", data.getResourceType());
            return null;
        }
        repository.getResources().put(instance.getId(), instance);
        repository.getSinceLastUse().put(instance.getId(), System.currentTimeMillis());
        return instance;
    }


    @Override
    public void removeResource(String id) {
        IResource resource = repository.getResources().get(id);
        if (resource == null) {
            getLogger().warn("Resource not found: {}", id);
            return;
        }
        switch (resource.getResourceType()) {
            case UBO: {
                uboService.remove((UBO) resource);
                break;
            }
            case MESH: {
                meshService.remove((Mesh) resource);
                break;
            }
            case MATERIAL: {
                materialService.remove((Material) resource);
                break;
            }
            case SHADER: {
                shaderService.remove((Shader) resource);
                break;
            }
            case AUDIO: {
                audioService.remove((Audio) resource);
                break;
            }
        }
    }

    @Override
    public <T extends IResource, R extends IResourceRuntimeData> void bind(T instance, R data) {
        switch (instance.getResourceType()) {
            case UBO: {
                uboService.bind((UBO) instance, (UBORuntimeData) data);
                break;
            }
            case MESH: {
                meshService.bind((Mesh) instance, (MeshRuntimeData) data);
                break;
            }
            case MATERIAL: {
                materialService.bind((Material) instance, (MaterialDTO) data);
                break;
            }
            case SHADER: {
                shaderService.bind((Shader) instance, (ShaderRuntimeData) data);
                break;
            }
            case AUDIO: {
                audioService.bind((Audio) instance, (AudioDTO) data);
                break;
            }
        }
    }

    @Override
    public <T extends IResource> void bind(T instance) {
        switch (instance.getResourceType()) {
            case UBO: {
                uboService.bind((UBO) instance);
                break;
            }
            case MESH: {
                meshService.bind((Mesh) instance);
                break;
            }
            case MATERIAL: {
                materialService.bind((Material) instance);
                break;
            }
            case SHADER: {
                shaderService.bind((Shader) instance);
                break;
            }
            case AUDIO: {
                audioService.bind((Audio) instance);
                break;
            }
        }
    }

    @Override
    public List<IResource> getAllByType(ResourceType type) {
        return repository.getResources().values().stream().filter(r -> r.getResourceType().equals(type)).collect(Collectors.toList());
    }

    @Scheduled(cron = "0 */5 * ? * *")
    public void removeUnused() {
        int removed = 0;
        for (var entry : repository.getSinceLastUse().entrySet()) {
            if (System.currentTimeMillis() - entry.getValue() > MAX_TIMEOUT) {
                removeResource(entry.getKey());
                removed++;
            }
        }
        getLogger().warn("Removed {} unused resources", removed);
        repository.getUsedResources().clear();
        repository.getResources().values().forEach(resource -> {
            repository.getUsedResources().putIfAbsent(resource.getResourceType(), new ArrayList<>());
            repository.getUsedResources().get(resource.getResourceType()).add(resource.getId());
        });
    }
}
