package com.pine.panels.files;

import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.component.MeshComponent;
import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.fs.ResourceEntry;
import com.pine.repository.fs.ResourceEntryType;
import com.pine.service.request.LoadSceneRequest;
import com.pine.service.streaming.mesh.MeshStreamableResource;
import com.pine.service.FilesService;
import com.pine.service.rendering.RequestProcessingService;
import com.pine.service.request.AddEntityRequest;
import com.pine.service.streaming.scene.SceneService;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.*;

import java.util.List;

public class FilesDirectoryPanel extends AbstractView {
    private static final int FLAGS = ImGuiTableFlags.ScrollY | ImGuiTableFlags.RowBg;
    private final ImVec4 DIRECTORY_COLOR = new ImVec4(
            1,
            0.8352941f,
            0.38039216f,
            1
    );

    @PInject
    public FilesService filesService;

    @PInject
    public WorldRepository worldRepository;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public RequestProcessingService requestProcessingService;

    @PInject
    public SceneService sceneService;

    private FilesContext context;
    protected boolean isWindowFocused;

    @Override
    public void onInitialize() {
        context = (FilesContext) getContext();
    }

    @Override
    public void render() {
        if (isWindowFocused && ImGui.isKeyPressed(ImGuiKey.Enter) && context.selected != null) {
            openResource(context.selected);
        }

        if (isWindowFocused && ImGui.isKeyPressed(ImGuiKey.Delete) && context.selected != null) {
            filesService.delete(context.selected);
        }
        if (ImGui.beginTable(imguiId, 5, FLAGS)) {
            ImGui.tableSetupColumn("", ImGuiTableColumnFlags.WidthFixed, 30f);
            ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Import date", ImGuiTableColumnFlags.WidthFixed, 100f);
            ImGui.tableSetupColumn("Type", ImGuiTableColumnFlags.WidthFixed, 100f);
            ImGui.tableSetupColumn("Size", ImGuiTableColumnFlags.WidthFixed, 100f);
            ImGui.tableHeadersRow();
            for (var child : context.currentDirectory.children) {
                renderChildren(child);
            }
            ImGui.endTable();
        }
    }

    private void renderChildren(ResourceEntry root) {
        ImGui.tableNextRow();
        if (context.selected == root) {
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg0, editorRepository.accentU32);
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg1, editorRepository.accentU32);
        }
        ImGui.tableNextColumn();
        if (root.type == ResourceEntryType.DIRECTORY) {
            ImGui.textColored(DIRECTORY_COLOR, root.type.getIcon());
        } else {
            ImGui.text(root.type.getIcon());
        }
        onClick(root);

        ImGui.tableNextColumn();
        ImGui.text(root.name);
        onClick(root);

        ImGui.tableNextColumn();
        ImGui.text(root.creationDateString);
        onClick(root);

        ImGui.tableNextColumn();
        ImGui.text(root.type.getLabel());
        onClick(root);

        ImGui.tableNextColumn();
        ImGui.text(root.type == ResourceEntryType.DIRECTORY ? "--" : root.sizeText);
        onClick(root);
    }

    private void onClick(ResourceEntry root) {
        if (ImGui.isItemHovered() && ImGui.isItemClicked()) {
            context.selected = root;
            if (root.type != ResourceEntryType.DIRECTORY) {
                editorRepository.inspectFile = root;
            } else {
                editorRepository.inspectFile = null;
            }
        }
        if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
            openResource(root);
        }
    }

    private void openResource(ResourceEntry root) {
        switch (root.type) {
            case DIRECTORY -> context.setDirectory(root);
            case SCENE ->
                    requestProcessingService.addRequest(new LoadSceneRequest(worldRepository.rootEntity, sceneService.stream(root.path)));
            case MESH -> {
                var request = new AddEntityRequest(List.of(ComponentType.MESH));
                requestProcessingService.addRequest(request);
                MeshComponent meshComponent = (MeshComponent) request.getResponse().components.get(MeshComponent.class.getSimpleName());
                meshComponent.lod0 = (MeshStreamableResource) root.streamableResource;
            }
        }
    }
}
