package com.pine.component.impl;

import com.pine.PInject;
import com.pine.component.AbstractFormField;
import com.pine.inspection.FieldDTO;
import com.pine.component.ResourceRef;
import com.pine.repository.ResourceLoaderRepository;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import imgui.ImGui;
import imgui.type.ImInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ResourceField extends AbstractFormField {
    @PInject
    public ResourceLoaderRepository repository;

    private final ImInt selected = new ImInt(-1);
    private final List<AbstractLoaderResponse.ResourceInfo> items = new ArrayList<>();
    private String[] itemsArr = new String[0];

    public ResourceField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
    }

    @Override
    public void onInitialize() {
        refresh();
        var cast = (ResourceRef) dto.getValue();

        if(cast != null) {
            for (int i = 0; i < items.size(); i++) {
                var item = items.get(i);
                if (Objects.equals(item.id, cast.id)) {
                    selected.set(i);
                }
            }
        }
    }

    private void refresh() {
        for (var history : repository.loadedResources) {
            items.addAll(history.getRecords());
        }
        itemsArr = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            var r = items.get(i);
            itemsArr[i] = r.name;
        }
    }

    @Override
    public void renderInternal() {
        ImGui.text(dto.getLabel());
        if (ImGui.combo(internalId, selected, itemsArr)) {
            changerHandler.accept(dto, new ResourceRef(items.get(selected.get()).id));
        }

        if (ImGui.button("Refresh" + internalId + "1")) {
            refresh();
        }
    }
}
