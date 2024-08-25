package com.pine.core.service;

import com.pine.common.serialization.SerializableRepository;
import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceCreationData;
import com.pine.core.service.repository.*;
import com.pine.core.service.repository.primitives.audio.AudioDTO;
import com.pine.core.service.repository.primitives.material.MaterialDTO;
import com.pine.core.service.repository.primitives.mesh.MeshDTO;
import com.pine.core.service.repository.primitives.shader.ShaderCreationDTO;
import com.pine.core.service.repository.primitives.terrain.TerrainCreationDTO;
import com.pine.core.service.repository.primitives.texture.TextureCreationDTO;
import com.pine.core.service.repository.primitives.ubo.UBOCreationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public abstract class ResourceRepository extends SerializableRepository {
    /**
     * 5 minutes
     */
    private static final long MAX_TIMEOUT = 5 * 60 * 1000;

    @Autowired
    private AudioRepository audioRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MeshRepository meshRepository;

    @Autowired
    private ShaderRepository shaderRepository;

    @Autowired
    private TerrainRepository terrainRepository;

    @Autowired
    private TextureRepository textureRepository;

    @Autowired
    private UBORepository uboRepository;

    private final Map<String, IResource> resources = new HashMap<>();
    private final Map<String, Long> sinceLastUse = new HashMap<>();
    private final Map<ResourceType, List<String>> usedResources = new HashMap<>();

    public <C extends IResourceCreationData, T extends IResource> T getResource(C data) {
        T instance = null;
        switch (data.getResourceType()) {
            case UBO: {
                instance = uboRepository.add((UBOCreationData) data);
                break;
            }
            case MESH: {
                instance = meshRepository.add((MeshDTO) data);
                break;
            }
            case MATERIAL: {
                instance = materialRepository.add((MaterialDTO) data);
                break;
            }
            case SHADER: {
                instance = shaderRepository.add((ShaderCreationDTO) data);
                break;
            }
            case AUDIO: {
                instance = audioRepository.add((AudioDTO) data);
                break;
            }
            case TERRAIN: {
                instance = terrainRepository.add((TerrainCreationDTO) data);
                break;
            }
            case TEXTURE: {
                instance = textureRepository.add((TextureCreationDTO) data);
                break;
            }
        }

        resources.put(instance.getId(), instance);
        sinceLastUse.put(instance.getId(), System.currentTimeMillis());
        return instance;
    }

    public Map<ResourceType, List<String>> getUsedResources() {
        return usedResources;
    }

    public void removeResource(String id) {
        IResource resource = resources.get(id);
        if (resource == null) {
            getLogger().warn("Resource not found: {}", id);
            return;
        }
        switch (resource.getResourceType()) {
            case UBO: {
                uboRepository.remove(id);
                break;
            }
            case MESH: {
                meshRepository.remove(id);
                break;
            }
            case MATERIAL: {
                materialRepository.remove(id);
                break;
            }
            case SHADER: {
                shaderRepository.remove(id);
                break;
            }
            case AUDIO: {
                audioRepository.remove(id);
                break;
            }
            case TERRAIN: {
                terrainRepository.remove(id);
                break;
            }
        }
    }

    @Scheduled(cron = "0 */5 * ? * *")
    void removeUnused() {
        int removed = 0;
        for (var entry : sinceLastUse.entrySet()) {
            if (System.currentTimeMillis() - entry.getValue() > MAX_TIMEOUT) {
                removeResource(entry.getKey());
                removed++;
            }
        }
        getLogger().warn("Removed {} unused resources", removed);
        usedResources.clear();
        resources.values().forEach(resource -> {
            usedResources.putIfAbsent(resource.getResourceType(), new ArrayList<>());
            usedResources.get(resource.getResourceType()).add(resource.getId());
        });
    }
}
