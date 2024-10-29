package com.pine.panels.component.impl;

import com.pine.injection.PInject;
import com.pine.inspection.FieldDTO;
import com.pine.inspection.ResourceTypeField;
import com.pine.panels.component.AbstractFormField;
import com.pine.repository.FilesRepository;
import com.pine.repository.fs.FileEntry;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.type.ImInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ResourceField extends AbstractFormField {

    @PInject
    public StreamingRepository repository;

    @PInject
    public FilesRepository filesRepository;

    private final StreamableResourceType type;
    private final ImInt selected = new ImInt(-1);
    private String[] itemsArr = new String[0];
    private int previousSize = -1;
    private final List<FileEntry> allByType = new ArrayList<>();

    public ResourceField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        type = dto.getField().getAnnotation(ResourceTypeField.class).type();
    }

    @Override
    public void onInitialize() {
        refresh();
    }

    private void refresh() {
        List<String> byType = filesRepository.byType.get(type);
        if (byType.size() != allByType.size()) {
            allByType.clear();
            for (var f : byType) {
                allByType.add((FileEntry) filesRepository.entry.get(f));
            }
            if (previousSize != allByType.size()) {
                previousSize = allByType.size();
                itemsArr = new String[allByType.size()];
                for (int i = 0, allByTypeSize = itemsArr.length; i < allByTypeSize; i++) {
                    var file = allByType.get(i);
                    itemsArr[i] = file.name;
                }

                String value = (String) dto.getValue();
                if (value != null) {
                    for (int i = 0; i < allByType.size(); i++) {
                        if (Objects.equals(allByType.get(i).getId(), value)) {
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
        ImGui.text(dto.getLabel());
        if (ImGui.combo(imguiId, selected, itemsArr)) {
            changerHandler.accept(dto, allByType.get(selected.get()).getId());
        }

        ImGui.sameLine();
        if (ImGui.button(Icons.close + "Remove" + imguiId)) {
            selected.set(-1);
            changerHandler.accept(dto, null);
        }
    }
}
