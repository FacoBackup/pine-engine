package com.pine.panels.component.impl;

import com.pine.injection.PInject;
import com.pine.inspection.FieldDTO;
import com.pine.inspection.TypePreviewField;
import com.pine.panels.component.AbstractFormField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.importer.ImporterService;
import com.pine.service.streaming.impl.TextureService;
import com.pine.service.streaming.ref.TextureResourceRef;
import imgui.ImGui;
import imgui.ImVec2;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

import static com.pine.panels.viewport.ViewportPanel.INV_X;
import static com.pine.panels.viewport.ViewportPanel.INV_Y;

public class PreviewField extends AbstractFormField {
    private String value;
    private TextureResourceRef textureRef;
    private final StreamableResourceType type;
    private final ImVec2 sizeVec = new ImVec2();
    @PInject
    public TextureService textureService;

    @PInject
    public ImporterService importerService;

    @PInject
    public StreamingRepository streamingRepository;

    public PreviewField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        type = dto.getField().getAnnotation(TypePreviewField.class).type();
    }

    @Override
    public void render() {
        if (dto.getValue() != null && !Objects.equals(value, dto.getValue())) {
            value = (String) dto.getValue();
            var texture = textureService.stream(value, Collections.emptyMap(), Collections.emptyMap());
            if (texture != null) {
                String id = UUID.randomUUID().toString();
                textureRef = new TextureResourceRef(id);
                textureRef.load(texture);
                streamingRepository.loadedResources.put(id, textureRef);
            }
        }

        if (textureRef != null) {
            float size = ImGui.getContentRegionAvailX() - 30;
            sizeVec.x = size;
            sizeVec.y = size;
            ImGui.image(textureRef.texture, sizeVec, INV_Y, INV_X);
        }
    }
}
