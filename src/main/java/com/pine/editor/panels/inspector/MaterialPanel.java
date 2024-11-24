package com.pine.editor.panels.inspector;

import com.pine.common.injection.PInject;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.repository.terrain.AbstractDataInstance;
import com.pine.engine.repository.terrain.MaterialInstance;
import com.pine.engine.repository.terrain.TerrainRepository;
import com.pine.common.Icons;

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
