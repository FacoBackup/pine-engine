package com.pine.panels.component.impl;

import com.pine.injection.PInject;
import com.pine.inspection.FieldDTO;
import com.pine.panels.component.AbstractFormField;
import com.pine.repository.ClockRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.ImporterService;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.ref.TextureResourceRef;
import imgui.ImGui;
import imgui.ImVec2;

import java.util.Objects;
import java.util.function.BiConsumer;

import static com.pine.panels.viewport.ViewportPanel.INV_Y;
import static com.pine.service.importer.impl.TextureImporter.PREVIEW_EXT;

public class PreviewField extends AbstractFormField {
    public static final ImVec2 INV_X_L = new ImVec2(-1, 0);
    private String value;
    private TextureResourceRef textureRef;
    private final ImVec2 sizeVec = new ImVec2();

    @PInject
    public ClockRepository clockRepository;

    @PInject
    public StreamingService streamingService;

    @PInject
    public ImporterService importerService;

    private boolean smallSize;

    public PreviewField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
    }

    @Override
    public void render() {
        if (dto.getValue() != null && !Objects.equals(value, dto.getValue())) {
            value = (String) dto.getValue();
            textureRef = streamingService.streamTextureSync(importerService.getPathToFile(value, StreamableResourceType.TEXTURE) + PREVIEW_EXT);
        }else if(dto.getValue() == null){
            value = null;
            textureRef = null;
        }

        if (textureRef != null) {
            textureRef.lastUse = clockRepository.totalTime;
            float size = ImGui.getContentRegionAvailX() - 30;
            sizeVec.x = smallSize ? 55 : size;
            sizeVec.y = smallSize ? 55 : size;
            ImGui.image(textureRef.texture, sizeVec, INV_X_L, INV_Y);
        }
    }

    public void setSmallSize(boolean small) {
        this.smallSize = small;
    }
}
