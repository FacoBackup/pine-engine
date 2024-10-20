package com.pine.panels.files;

import com.pine.component.ComponentType;
import com.pine.component.MeshComponent;
import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.FileMetadataRepository;
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
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractDirectoryPanel extends AbstractView {
    protected final ImVec4 DIRECTORY_COLOR = new ImVec4(
            1,
            0.8352941f,
            0.38039216f,
            1
    );

    @PInject
    public FilesService filesService;
    @PInject
    public FileMetadataRepository fileMetadataRepository;
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

    protected FilesContext context;
    protected boolean isWindowFocused;
    protected List<FileEntry> filesLocal = new ArrayList<>();

    @Override
    public void onInitialize() {
        context = (FilesContext) getContext();
    }

    protected void hotkeys() {
        if (!isWindowFocused) {
            return;
        }
        if (ImGui.isKeyPressed(ImGuiKey.Enter) && !context.selected.isEmpty()) {
            openSelected();
        }

        if (ImGui.isKeyPressed(ImGuiKey.Delete) && !context.selected.isEmpty()) {
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
        for (var entry : context.toCut.entrySet()) {
            String key = entry.getKey();
            DirectoryEntry value = entry.getValue();
            if (value.directories.containsKey(key)) {
                var entryData = value.directories.get(key);
                value.directories.remove(key);
                context.currentDirectory.directories.put(entryData.id, entryData);
            } else {
                value.files.remove(key);
                context.currentDirectory.files.add(key);
            }
        }
        context.toCut.clear();
    }

    private void openSelected() {
        for (String id : context.selected.keySet()) {
            var file = fileMetadataRepository.getFile(id);
            if (file != null) {
                openResource(file);
            }
        }
    }

    private void cutSelected() {
        context.toCut.clear();
        for (var file : context.selected.keySet()) {
            context.toCut.put(file, context.currentDirectory);
        }
    }

    private void selectAll() {
        for (var file : context.currentDirectory.files) {
            context.selected.put(file, true);
        }

        for (var directory : context.currentDirectory.directories.keySet()) {
            context.selected.put(directory, true);
        }
    }

    private void deleteSelected() {
        filesService.deleteSelected(context);
        filesLocal.clear();
    }

    protected void onClick(IEntry root) {
        if (ImGui.isItemHovered() && ImGui.isItemClicked()) {
            if (!ImGui.isKeyDown(ImGuiKey.LeftCtrl)) {
                context.selected.clear();
            }
            context.selected.put(root.getId(), true);
            if (root instanceof DirectoryEntry) {
                context.setInspection(null);
            } else {
                context.setInspection((FileEntry) root);
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
        if (root instanceof DirectoryEntry) {
            context.setDirectory((DirectoryEntry) root);
            context.selected.clear();
        } else {
            switch (((FileEntry) root).metadata.getResourceType()) {
                case SCENE -> {
                    var scene = (SceneImportData) sceneService.stream(importerService.getPathToFile(root.getId(), StreamableResourceType.SCENE), Collections.emptyMap(), Collections.emptyMap());
                    requestProcessingService.addRequest(new LoadSceneRequest(worldRepository.rootEntity, scene));
                }
                case MESH -> {
                    var request = new AddEntityRequest(List.of(ComponentType.MESH));
                    requestProcessingService.addRequest(request);
                    MeshComponent meshComponent = (MeshComponent) request.getResponse().components.get(ComponentType.MESH);
                    meshComponent.lod0 = root.getId();
                }
            }
        }
    }

    protected void updateFiles() {
        if (!fileMetadataRepository.isLoading() && filesLocal.size() != context.currentDirectory.files.size()) {
            filesLocal.clear();
            for (var file : context.currentDirectory.files) {
                FileEntry fileEntry = fileMetadataRepository.getFile(file);
                if (fileEntry != null) {
                    filesLocal.add(fileEntry);
                }
            }

            if (context.currentDirectory.files.size() != filesLocal.size()) {
                context.currentDirectory.files = filesLocal.stream().map(FileEntry::getId).collect(Collectors.toSet());
                ;
            }
        }
    }
}
