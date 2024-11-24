package com.pine.editor.panels.files;

import com.pine.FSUtil;
import com.pine.common.injection.PInject;
import com.pine.common.inspection.FieldDTO;
import com.pine.editor.core.AbstractView;
import com.pine.editor.panels.component.FormPanel;
import com.pine.editor.repository.FSEntry;
import com.pine.engine.repository.streaming.AbstractResourceRef;
import com.pine.engine.repository.streaming.StreamingRepository;
import com.pine.engine.service.importer.ImporterService;
import com.pine.engine.service.importer.metadata.AbstractResourceMetadata;
import com.pine.engine.service.request.UpdateFieldRequest;
import imgui.ImGui;
import imgui.type.ImString;

public class FileInspectorPanel extends AbstractView {

    private FormPanel dataForm;
    private FormPanel metadataForm;
    private AbstractResourceMetadata currentMetadata;

    @PInject
    public ImporterService importerService;

    @PInject
    public StreamingRepository streamingRepository;

    private FSEntry inspection;
    private final ImString inspectionName = new ImString();

    public void setInspection(FSEntry inspection) {
        if (this.inspection != inspection) {
            if (inspection == null) {
                dataForm.setInspection(null);
                currentMetadata = null;
                return;
            }
            currentMetadata = (AbstractResourceMetadata) FSUtil.readBinary(importerService.getPathToMetadata(inspection.id, inspection.getType()));
            if (currentMetadata != null) {
                if (currentMetadata.getResourceType().isMutable()) {
                    dataForm.setInspection(importerService.readFile(currentMetadata));
                } else {
                    dataForm.setInspection(null);
                }
                metadataForm.setInspection(currentMetadata);
            }
        }
        this.inspection = inspection;
        if (inspection != null) {
            inspectionName.set(inspection.name);
        }
    }

    @Override
    public void onInitialize() {
        appendChild(dataForm = new FormPanel(this::handleChange));
        appendChild(metadataForm = new FormPanel(this::handleMetadataChange));
    }

    private void handleChange(FieldDTO dto, Object object) {
        onChange(dto, object, importerService.getPathToFile(currentMetadata.id, currentMetadata.getResourceType()), true);
    }

    private void handleMetadataChange(FieldDTO dto, Object object) {
        onChange(dto, object, importerService.getPathToMetadata(currentMetadata), false);
    }

    private void onChange(FieldDTO dto, Object object, String path, boolean dispose) {
        try {
            UpdateFieldRequest.process(dto, object);
            FSUtil.writeBinary(dto.getInstance(), path);
            if (dispose) {
                streamingRepository.discardedResources.remove(currentMetadata.id);
                AbstractResourceRef<?> ref = streamingRepository.streamed.get(currentMetadata.id);
                if (ref != null) {
                    ref.dispose();
                }
                streamingRepository.streamed.remove(currentMetadata.id);
            }
        } catch (Exception e) {
            getLogger().error("Error while updating metadata file {}", dataForm.getInspectable().getTitle(), e);
        }
    }

    @Override
    public void render() {
        if (ImGui.beginChild(imguiId)) {
            if (inspection != null) {
                ImGui.textDisabled(inspection.id);
                if (ImGui.inputText("##name" + imguiId, inspectionName)) {
                    inspection.name = inspectionName.get();
                }
            }
            super.render();
        }
        ImGui.endChild();
    }
}
