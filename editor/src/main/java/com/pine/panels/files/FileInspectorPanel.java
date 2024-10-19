package com.pine.panels.files;

import com.pine.FSUtil;
import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.inspection.FieldDTO;
import com.pine.panels.component.FormPanel;
import com.pine.service.importer.ImporterService;
import com.pine.service.importer.metadata.AbstractResourceMetadata;
import imgui.ImGui;

public class FileInspectorPanel extends AbstractView {

    private FilesContext context;
    private FormPanel form;
    private AbstractResourceMetadata currentMetadata;

    @PInject
    public ImporterService importerService;

    @Override
    public void onInitialize() {
        context = (FilesContext) getContext();
        appendChild(form = new FormPanel(this::handleChange));
        context.subscribe(() -> {
            currentMetadata = context.inspection.metadata;
            if (currentMetadata.getResourceType().isMutable()) {
                form.setInspectable(importerService.readFile(currentMetadata));
            } else {
                form.setInspectable(null);
            }
        });
    }

    private void handleChange(FieldDTO dto, Object object) {
        try {
            dto.getField().set(dto.getInstance(), object);
            FSUtil.write(dto.getInstance(), importerService.getPathToFile(currentMetadata.id, currentMetadata.getResourceType()));
        } catch (Exception e) {
            getLogger().error("Error while updating file {}", form.getInspectable().getTitle());
        }
    }

    @Override
    public void render() {
        if (ImGui.beginChild(imguiId)) {
            if(form.getInspectable() != null) {
                super.render();
            }else{
                ImGui.text("Select a file");
            }
            ImGui.endChild();
        }
    }
}
