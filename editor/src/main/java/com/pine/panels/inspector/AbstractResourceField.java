package com.pine.panels.inspector;

import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.FSEntry;
import com.pine.repository.FilesRepository;
import com.pine.repository.FoliageInstance;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.type.ImInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractResourceField extends AbstractView {
    @PInject
    public StreamingRepository repository;

    @PInject
    public FilesRepository filesRepository;

    private final ImInt selected = new ImInt(0);
    private String[] itemsArr = new String[0];
    private int previousSize = -1;
    private final List<FSEntry> allByType = new ArrayList<>();
    protected FoliageInstance foliage;

    public void setFoliage(FoliageInstance foliage) {
        this.foliage = foliage;
    }

    public abstract String getSelected();

    public abstract void setSelected(String selected);

    public abstract StreamableResourceType getType();

    private void refresh() {
        List<String> byType = filesRepository.byType.get(getType());
        if (byType.size() != allByType.size()) {
            allByType.clear();
            for (var f : byType) {
                allByType.add(filesRepository.entry.get(f));
            }
            if (previousSize != allByType.size()) {
                previousSize = allByType.size();
                itemsArr = new String[allByType.size() + 1];
                itemsArr[0] = "None";
                for (int i = 0, allByTypeSize = allByType.size(); i < allByTypeSize; i++) {
                    var file = allByType.get(i);
                    itemsArr[i + 1] = file.name;
                }

                if (getSelected() != null) {
                    for (int i = 0; i < allByType.size(); i++) {
                        if (Objects.equals(allByType.get(i).getId(), getSelected())) {
                            selected.set(i);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render() {
        refresh();
        if (ImGui.combo(imguiId + foliage.id, selected, itemsArr)) {
            if(selected.get() == 0){
                setSelected(null);
            }else {
                setSelected(allByType.get(selected.get() - 1).getId());
            }
        }
    }
}
