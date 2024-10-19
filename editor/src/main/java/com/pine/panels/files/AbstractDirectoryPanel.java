package com.pine.panels.files;

import com.pine.Engine;
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
import com.pine.service.FilesService;
import com.pine.service.rendering.RequestProcessingService;
import com.pine.service.request.AddEntityRequest;
import com.pine.service.request.LoadSceneRequest;
import com.pine.service.streaming.scene.SceneService;
import com.pine.service.streaming.scene.SceneStreamData;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public Engine engine;

    protected FilesContext context;
    protected boolean isLoading;
    protected boolean ready;
    protected boolean isWindowFocused;
    protected List<FileEntry> filesLocal = new ArrayList<>();

    @Override
    public void onInitialize() {
        context = (FilesContext) getContext();
    }

    protected void hotkeys() {
        if (isWindowFocused && ImGui.isKeyPressed(ImGuiKey.Enter) && !context.selected.isEmpty()) {
            openResource(context.selected
                    .values()
                    .stream()
                    .filter(a -> (a instanceof FileEntry))
                    .findFirst()
                    .orElse(null));
        }

        if (isWindowFocused && ImGui.isKeyPressed(ImGuiKey.Delete) && !context.selected.isEmpty()) {
            filesService.delete(context.selected);
        }
    }

    protected void onClick(IEntry root) {
        if (ImGui.isItemHovered() && ImGui.isItemClicked()) {
            if (!ImGui.isKeyDown(ImGuiKey.LeftCtrl)) {
                context.selected.clear();
            }
            context.selected.put(root.getId(), root);
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
                case SCENE ->
                        requestProcessingService.addRequest(new LoadSceneRequest(worldRepository.rootEntity, (SceneStreamData) sceneService.stream(((FileEntry) root).path, Collections.emptyMap())));
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
        if (filesLocal.size() != context.currentDirectory.files.size() && !isLoading) {
            filesLocal.clear();
            isLoading = true;
            for (var file : context.currentDirectory.files) {
                filesLocal.add(fileMetadataRepository.getFile(file));
            }
        }
    }
}
