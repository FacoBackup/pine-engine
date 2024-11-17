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

public class MaterialField extends AbstractView {
    @PInject
    public StreamingRepository repository;

    @PInject
    public FilesRepository filesRepository;

    private final ImInt selected = new ImInt(-1);
    private String[] itemsArr = new String[0];
    private int previousSize = -1;
    private final List<FSEntry> allByType = new ArrayList<>();
    private FoliageInstance foliage;

    public void setFoliage(FoliageInstance foliage) {
        this.foliage = foliage;
    }

    private void refresh() {
        List<String> byType = filesRepository.byType.get(StreamableResourceType.MATERIAL);
        if (byType.size() != allByType.size()) {
            allByType.clear();
            for (var f : byType) {
                allByType.add(filesRepository.entry.get(f));
            }
            if (previousSize != allByType.size()) {
                previousSize = allByType.size();
                itemsArr = new String[allByType.size()];
                for (int i = 0, allByTypeSize = itemsArr.length; i < allByTypeSize; i++) {
                    var file = allByType.get(i);
                    itemsArr[i] = file.name;
                }

                if (foliage.material != null) {
                    for (int i = 0; i < allByType.size(); i++) {
                        if (Objects.equals(allByType.get(i).getId(), foliage.material)) {
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
            foliage.material = allByType.get(selected.get()).getId();
        }
        ImGui.sameLine();
        if (ImGui.button(Icons.close + "Remove" + imguiId + foliage.id)) {
            selected.set(-1);
            foliage.material = null;
        }
    }
}
