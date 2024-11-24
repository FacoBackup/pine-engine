package com.pine.editor.panels.inspector;

import com.pine.common.injection.PInject;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.repository.terrain.AbstractDataInstance;
import com.pine.engine.repository.terrain.FoliageInstance;
import com.pine.engine.repository.terrain.TerrainRepository;
import com.pine.common.Icons;

import java.util.Map;

public class FoliagePanel extends AbstractTerrainDataPanel {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public TerrainRepository terrainRepository;

    @Override
    protected Map<String, ? extends AbstractDataInstance> getDataMap() {
        return terrainRepository.foliage;
    }

    @Override
    protected String getSelectedId() {
        return editorRepository.foliageForPainting;
    }

    @Override
    protected void setSelectedId(String id) {
        editorRepository.foliageForPainting = id;
    }

    @Override
    protected void addNewInstance(int index) {
        var instance = new FoliageInstance(index);
        terrainRepository.foliage.put(instance.id, instance);
    }

    @Override
    protected String getTitle() {
        return Icons.forest +  "Foliage";
    }
}
