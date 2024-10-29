package com.pine.panels.files;

import com.pine.component.ComponentType;
import com.pine.component.MeshComponent;
import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.FilesRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.fs.DirectoryEntry;
import com.pine.repository.fs.FileEntry;
import com.pine.repository.fs.IEntry;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.FilesService;
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

import java.util.*;

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
    public WorldRepository worldRepository;
    @PInject
    public EditorRepository editorRepository;
    @PInject
    public RequestProcessingService requestProcessingService;
    @PInject
    public SceneService sceneService;
    @PInject
    public ImporterService importerService;

    public boolean isWindowFocused;
    public String currentDirectory;
    public Map<String, Boolean> selected;
    public Map<String, Boolean> toCut;
    public FileEntry inspection;

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
                    IEntry fEntry = filesRepository.entry.get(child);
                    if (fEntry.isDirectory()) {
                        renderDirectory((DirectoryEntry) fEntry);
                    } else {
                        renderFile((FileEntry) fEntry);
                    }
                }
            }
            ImGui.endTable();
        }
    }

    private void renderDirectory(DirectoryEntry root) {
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

    private void renderFile(FileEntry root) {
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

    private void textColumn(String Directory, IEntry root) {
        ImGui.tableNextColumn();
        ImGui.text(Directory);
        onClick(root);
    }

    private void textDisabledColumn(String label, IEntry entry) {
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

    protected void onClick(IEntry root) {
        if (ImGui.isItemHovered() && ImGui.isItemClicked()) {
            if (!ImGui.isKeyDown(ImGuiKey.LeftCtrl)) {
                selected.clear();
            }
            selected.put(root.getId(), true);
            if (root.isDirectory()) {
                inspection = null;
            } else {
                inspection = (FileEntry) root;
            }
        }
        if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
            openResource(root);
        }
    }

    protected void openResource(IEntry root) {
        if (root == null) {
            return;
        }
        if (root.isDirectory()) {
            currentDirectory = ((DirectoryEntry) root).id;
            selected.clear();
        } else {
            switch (((FileEntry) root).getType()) {
                case SCENE -> {
                    var scene = (SceneImportData) sceneService.stream(importerService.getPathToFile(root.getId(), StreamableResourceType.SCENE), Collections.emptyMap(), Collections.emptyMap());
                    requestProcessingService.addRequest(new LoadSceneRequest(worldRepository.rootEntity, scene));
                }
                case MESH -> {
                    var request = new AddEntityRequest(List.of(ComponentType.MESH));
                    requestProcessingService.addRequest(request);

                    MeshComponent meshComponent = (MeshComponent) worldRepository.components.get(ComponentType.MESH).get(request.getResponse().id());
                    meshComponent.lod0 = root.getId();
                }
            }
        }
    }
}
