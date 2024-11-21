package com.pine.panels.inspector;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.terrain.AbstractDataInstance;
import com.pine.repository.terrain.MaterialInstance;
import com.pine.repository.terrain.TerrainRepository;
import com.pine.theme.Icons;

import java.util.Map;

public class MaterialPanel extends AbstractTerrainDataPanel {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public TerrainRepository terrainRepository;

    @Override
    protected Map<String, ? extends AbstractDataInstance> getDataMap() {
        return terrainRepository.materials;
    }

    @Override
    protected String getSelectedId() {
        return editorRepository.materialForPainting;
    }

    @Override
    protected void setSelectedId(String id) {
        editorRepository.materialForPainting = id;
    }

    @Override
    protected void addNewInstance(int index) {
        var instance = new MaterialInstance(index);
        terrainRepository.materials.put(instance.id, instance);
    }

    @Override
    protected String getTitle() {
        return Icons.format_paint +  "Materials";
    }
}
