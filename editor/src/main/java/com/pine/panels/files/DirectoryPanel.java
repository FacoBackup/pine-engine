package com.pine.panels.files;

import com.pine.component.ComponentType;
import com.pine.component.MeshComponent;
import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.FSEntry;
import com.pine.repository.FilesRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.FilesService;
import com.pine.service.grid.WorldService;
import com.pine.service.importer.ImporterService;
import com.pine.service.importer.data.SceneImportData;
import com.pine.service.rendering.RequestProcessingService;
import com.pine.service.request.AddEntityRequest;
import com.pine.service.request.LoadSceneRequest;
import com.pine.service.streaming.impl.SceneService;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DirectoryPanel extends AbstractView {
    private static final ImVec4 DIRECTORY_COLOR = new ImVec4(
            1,
            0.8352941f,
            0.38039216f,
            1
    );
    private static final int FLAGS = ImGuiTableFlags.ScrollY | ImGuiTableFlags.RowBg;

    @PInject
    public FilesService filesService;
    @PInject
    public FilesRepository filesRepository;
    @PInject
    public WorldService worldService;
    @PInject
    public EditorRepository editorRepository;
    @PInject
    public RequestProcessingService requestProcessingService;
    @PInject
    public SceneService sceneService;
    @PInject
    public ImporterService importerService;
    @PInject
    public WorldRepository worldRepository;

    public boolean isWindowFocused;
    public String currentDirectory;
    public Map<String, Boolean> selected;
    public Map<String, Boolean> toCut;
    public FSEntry inspection;

    @Override
    public void render() {
        hotkeys();

        if (ImGui.beginTable(imguiId, 5, FLAGS)) {
            ImGui.tableSetupColumn("", ImGuiTableColumnFlags.WidthFixed, 30f);
            ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Import date", ImGuiTableColumnFlags.WidthFixed, 100f);
            ImGui.tableSetupColumn("Type", ImGuiTableColumnFlags.WidthFixed, 100f);
            ImGui.tableSetupColumn("Size", ImGuiTableColumnFlags.WidthFixed, 100f);
            ImGui.tableHeadersRow();

            List<String> children = filesRepository.parentChildren.get(currentDirectory);
            if (children != null) {
                for (var child : children) {
                    FSEntry fEntry = filesRepository.entry.get(child);
                    if (fEntry.isDirectory()) {
                        renderDirectory(fEntry);
                    } else {
                        renderFile(fEntry);
                    }
                }
            }
            ImGui.endTable();
        }
    }

    private void renderDirectory(FSEntry root) {
        ImGui.tableNextRow();
        if (selected.containsKey(root.getId())) {
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg0, editorRepository.accentU32);
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg1, editorRepository.accentU32);
        }

        ImGui.tableNextColumn();
        ImGui.textColored(DIRECTORY_COLOR, Icons.folder);
        onClick(root);
        if (toCut.containsKey(root.id)) {
            textDisabledColumn(root.name, root);
            textDisabledColumn("--", root);
            textDisabledColumn("Directory", root);
            textDisabledColumn("--", root);
        } else {
            textColumn(root.name, root);
            textColumn("--", root);
            textColumn("Directory", root);
            textColumn("--", root);
        }
    }

    private void renderFile(FSEntry root) {
        StreamableResourceType resourceType = root.getType();
        ImGui.tableNextRow();
        if (selected.containsKey(root.getId())) {
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg0, editorRepository.accentU32);
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg1, editorRepository.accentU32);
        }
        if (toCut.containsKey(root.getId())) {
            textDisabledColumn(resourceType.getIcon(), root);
            textDisabledColumn(root.name, root);
            textDisabledColumn(root.creationDateString, root);
            textDisabledColumn(resourceType.getTitle(), root);
            textDisabledColumn(root.sizeText, root);
        } else {
            textColumn(resourceType.getIcon(), root);
            textColumn(root.name, root);
            textColumn(root.creationDateString, root);
            textColumn(resourceType.getTitle(), root);
            textColumn(root.sizeText, root);
        }
    }

    private void textColumn(String Directory, FSEntry root) {
        ImGui.tableNextColumn();
        ImGui.text(Directory);
        onClick(root);
    }

    private void textDisabledColumn(String label, FSEntry entry) {
        ImGui.tableNextColumn();
        ImGui.textDisabled(label);
        onClick(entry);
    }

    protected void hotkeys() {
        if (!isWindowFocused) {
            return;
        }
        if (ImGui.isKeyPressed(ImGuiKey.Enter) && !selected.isEmpty()) {
            openSelected();
        }

        if (ImGui.isKeyPressed(ImGuiKey.Delete) && !selected.isEmpty()) {
            deleteSelected();
        }

        if (ImGui.isKeyDown(ImGuiKey.LeftCtrl) && ImGui.isKeyPressed(ImGuiKey.A)) {
            selectAll();
        }

        if (ImGui.isKeyDown(ImGuiKey.LeftCtrl) && ImGui.isKeyPressed(ImGuiKey.X)) {
            cutSelected();
        }

        if (ImGui.isKeyDown(ImGuiKey.LeftCtrl) && ImGui.isKeyPressed(ImGuiKey.V)) {
            pasteSelected();
        }
    }

    private void pasteSelected() {
        for (var key : toCut.keySet()) {
            var children = filesRepository.parentChildren.get(currentDirectory);
            if (!children.contains(key)) {
                var previousParentId = filesRepository.childParent.get(key);
                filesRepository.parentChildren.get(previousParentId).remove(key);

                children.add(key);
                filesRepository.childParent.put(key, currentDirectory);
            }
        }
        toCut.clear();
    }

    private void openSelected() {
        for (String id : selected.keySet()) {
            openResource(filesRepository.entry.get(id));
        }
    }

    private void cutSelected() {
        toCut.clear();
        toCut.putAll(selected);
    }

    private void selectAll() {
        var children = filesRepository.parentChildren.get(currentDirectory);
        for (var child : children) {
            selected.put(child, true);
        }
    }

    private void deleteSelected() {
        filesService.deleteSelected(selected.keySet());
    }

    protected void onClick(FSEntry root) {
        if (ImGui.isItemHovered() && ImGui.isItemClicked()) {
            if (!ImGui.isKeyDown(ImGuiKey.LeftCtrl)) {
                selected.clear();
            }
            selected.put(root.getId(), true);
            if (root.isDirectory()) {
                inspection = null;
            } else {
                inspection = root;
            }
        }
        if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
            openResource(root);
        }
    }

    protected void openResource(FSEntry root) {
        if (root == null) {
            return;
        }
        if (root.isDirectory()) {
            currentDirectory = root.id;
            selected.clear();
        } else {
            switch (root.getType()) {
                case SCENE -> {
                    var scene = (SceneImportData) sceneService.stream(importerService.getPathToFile(root.getId(), StreamableResourceType.SCENE), Collections.emptyMap(), Collections.emptyMap());
                    requestProcessingService.addRequest(new LoadSceneRequest(worldRepository.entityMap.get(WorldRepository.ROOT_ID), scene));
                }
                case MESH -> {
                    var request = new AddEntityRequest(List.of(ComponentType.MESH));
                    requestProcessingService.addRequest(request);

                    MeshComponent meshComponent = worldRepository.bagMeshComponent.get(request.getResponse().id());
                    meshComponent.lod0 = root.getId();
                }
            }
        }
    }
}
