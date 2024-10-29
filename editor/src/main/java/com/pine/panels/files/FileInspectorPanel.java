package com.pine.panels.files;

import com.pine.FSUtil;
import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.inspection.FieldDTO;
import com.pine.panels.component.FormPanel;
import com.pine.repository.fs.FileEntry;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.importer.ImporterService;
import com.pine.service.importer.metadata.AbstractResourceMetadata;
import com.pine.service.request.UpdateFieldRequest;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

public class FileInspectorPanel extends AbstractView {

    private FormPanel dataForm;
    private FormPanel metadataForm;
    private AbstractResourceMetadata currentMetadata;

    @PInject
    public ImporterService importerService;

    @PInject
    public StreamingRepository streamingRepository;

    private FileEntry inspection;
    private final ImString inspectionName = new ImString();

    public void setInspection(FileEntry inspection) {
        if (this.inspection != inspection) {
            if (inspection == null) {
                dataForm.setInspection(null);
                currentMetadata = null;
                return;
            }
            currentMetadata = FSUtil.read(importerService.getPathToMetadata(inspection.id, inspection.getType()), inspection.getType().getMetadataClazz());
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
            UpdateFieldRequest.process(dto, object, null);
            FSUtil.write(dto.getInstance(), path);
            if (dispose) {
                streamingRepository.discardedResources.remove(currentMetadata.id);
                AbstractResourceRef<?> ref = streamingRepository.loadedResources.get(currentMetadata.id);
                if (ref != null) {
                    ref.dispose();
                }
                streamingRepository.loadedResources.remove(currentMetadata.id);
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
                if (ImGui.inputText("##name" + imguiId, inspectionName, ImGuiInputTextFlags.EnterReturnsTrue)) {
                    inspection.name = inspectionName.get();
                }
            }
            super.render();
        }
        ImGui.endChild();
    }
}
