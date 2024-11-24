package com.pine.editor.panels.component.impl;

import com.pine.common.Icons;
import com.pine.common.injection.PInject;
import com.pine.common.inspection.FieldDTO;
import com.pine.editor.panels.component.AbstractFormField;
import com.pine.editor.repository.FSEntry;
import com.pine.editor.repository.FilesRepository;
import com.pine.engine.inspection.ResourceTypeField;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.repository.streaming.StreamingRepository;
import imgui.ImGui;
import imgui.type.ImInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ResourceField extends AbstractFormField {

    private PreviewField previewField;

    @PInject
    public StreamingRepository repository;

    @PInject
    public FilesRepository filesRepository;

    private final StreamableResourceType type;
    private final ImInt selected = new ImInt(-1);
    private String[] itemsArr = new String[0];
    private int previousSize = -1;
    private final List<FSEntry> allByType = new ArrayList<>();

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
                allByType.add(filesRepository.entry.get(f));
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

        if (type == StreamableResourceType.TEXTURE) {
            ImGui.text(dto.getLabel());

            ImGui.columns(2, "##resourceColumns" + imguiId, false);
            ImGui.setColumnWidth(0, 65);
            if (previewField == null) {
                previewField = appendChild(new PreviewField(dto, changerHandler));
                previewField.setSmallSize(true);
            }
            previewField.render();
            ImGui.nextColumn();
            renderCombo();
            renderRemove();
            ImGui.columns(1);
        } else {
            ImGui.text(dto.getLabel());
            renderCombo();
            ImGui.sameLine();
            renderRemove();
        }
    }

    private void renderRemove() {
        if (ImGui.button(Icons.close + "Remove" + imguiId)) {
            selected.set(-1);
            changerHandler.accept(dto, null);
        }
    }

    private void renderCombo() {
        if (ImGui.combo(imguiId, selected, itemsArr)) {
            changerHandler.accept(dto, allByType.get(selected.get()).getId());
        }
    }
}
