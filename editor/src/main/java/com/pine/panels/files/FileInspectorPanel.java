package com.pine.panels.files;

import com.pine.FSUtil;
import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.inspection.FieldDTO;
import com.pine.panels.component.FormPanel;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.importer.ImporterService;
import com.pine.service.importer.metadata.AbstractResourceMetadata;
import com.pine.service.request.UpdateFieldRequest;
import imgui.ImGui;

public class FileInspectorPanel extends AbstractView {

    private FilesContext context;
    private FormPanel dataForm;
    private FormPanel metadataForm;
    private AbstractResourceMetadata currentMetadata;

    @PInject
    public ImporterService importerService;

    @PInject
    public StreamingRepository streamingRepository;

    @Override
    public void onInitialize() {
        context = (FilesContext) getContext();
        appendChild(dataForm = new FormPanel(this::handleChange));
        appendChild(metadataForm = new FormPanel(this::handleMetadataChange));
        context.subscribe(() -> {
            if (context.inspection == null) {
                dataForm.setInspectable(null);
                currentMetadata = null;
                return;
            }
            currentMetadata = context.inspection.metadata;
            if (currentMetadata.getResourceType().isMutable()) {
                dataForm.setInspectable(importerService.readFile(currentMetadata));
            } else {
                dataForm.setInspectable(null);
            }

            metadataForm.setInspectable(currentMetadata);
        });
    }

    private void handleChange(FieldDTO dto, Object object) {
        onChange(dto, object, importerService.getPathToFile(currentMetadata.id, currentMetadata.getResourceType()), true);
    }

    private void handleMetadataChange(FieldDTO dto, Object object) {
        onChange(dto, object, importerService.getPathToMetadata(currentMetadata.id), false);
    }

    private void onChange(FieldDTO dto, Object object, String path, boolean dispose) {
        try {
            UpdateFieldRequest.process(dto, object);
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
            super.render();
        }
        ImGui.endChild();
    }
}
