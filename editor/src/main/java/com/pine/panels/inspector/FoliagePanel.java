package com.pine.panels.inspector;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.terrain.AbstractDataInstance;
import com.pine.repository.terrain.FoliageInstance;
import com.pine.repository.terrain.TerrainRepository;
import com.pine.theme.Icons;

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
