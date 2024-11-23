package com.pine.panels.files;

import com.pine.component.ComponentType;
import com.pine.component.MeshComponent;
import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.panels.component.impl.PreviewField;
import com.pine.repository.*;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.FilesService;
import com.pine.service.ThemeService;
import com.pine.service.importer.ImporterService;
import com.pine.service.importer.data.SceneImportData;
import com.pine.service.rendering.RequestProcessingService;
import com.pine.service.request.AddEntityRequest;
import com.pine.service.request.LoadSceneRequest;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.impl.SceneService;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.pine.panels.viewport.ViewportPanel.INV_Y;

public class DirectoryPanel extends AbstractView {
    private static final ImVec4 DIRECTORY_COLOR = new ImVec4(
            1,
            0.8352941f,
            0.38039216f,
            1
    );
    private static final int CARD_SIZE = 90;
    private static final int TEXT_OFFSET = 28;
    private static final ImVec2 TEXTURE_SIZE = new ImVec2(CARD_SIZE - 15, CARD_SIZE - TEXT_OFFSET - 4);

    @PInject
    public FilesService filesService;
    @PInject
    public ClockRepository clockRepository;
    @PInject
    public ThemeService themeService;
    @PInject
    public FilesRepository filesRepository;
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
    @PInject
    public StreamingService streamingService;

    public boolean isWindowFocused;
    public String currentDirectory;
    public Map<String, Boolean> selected;
    public Map<String, Boolean> toCut;
    public FSEntry inspection;
    private boolean isSomethingHovered;

    @Override
    public void render() {
        if (ImGui.beginChild(imguiId)) {
            isSomethingHovered = ImGui.isWindowHovered();
            if (ImGui.isWindowFocused()) {
                selected.clear();
                inspection = null;
            }
            float size = Math.round(ImGui.getWindowSizeX() / CARD_SIZE) * CARD_SIZE - CARD_SIZE;
            List<String> children = filesRepository.parentChildren.get(currentDirectory);
            if (children != null) {
                int rowIndex = 1;
                for (String child : children) {
                    FSEntry fEntry = filesRepository.entry.get(child);
                    boolean isSelected = selected.containsKey(child);
                    ImGui.pushStyleColor(ImGuiCol.ChildBg, isSelected || fEntry.isHovered ? editorRepository.accent : themeService.palette0);
                    renderItem(child, fEntry);
                    ImGui.popStyleColor();
                    if (rowIndex * CARD_SIZE < size) {
                        ImGui.sameLine();
                        rowIndex++;
                    } else {
                        rowIndex = 0;
                    }
                }
            }
            hotkeys();
        }
        ImGui.endChild();
    }

    private void renderItem(String child, FSEntry fEntry) {
        if (ImGui.beginChild(child, CARD_SIZE, CARD_SIZE + 15, true)) {
            fEntry.isHovered = ImGui.isWindowHovered();
            isSomethingHovered = isSomethingHovered || fEntry.isHovered;
            onClick(fEntry);
            if (fEntry.isDirectory()) {
                ImGui.textColored(DIRECTORY_COLOR, Icons.folder);
            } else if (fEntry.type == StreamableResourceType.TEXTURE) {
                var texture = streamingService.streamIn(child, StreamableResourceType.TEXTURE);
                if (texture != null) {
                    texture.lastUse = clockRepository.totalTime;
                    ImGui.image(((TextureResourceRef) texture).texture, TEXTURE_SIZE, PreviewField.INV_X_L, INV_Y);
                }
            } else {
                ImGui.text(fEntry.getType().getIcon());
            }
            ImGui.dummy(0, ImGui.getContentRegionAvailY() - TEXT_OFFSET);
            ImGui.separator();
            if (toCut.containsKey(child)) {
                ImGui.textDisabled(fEntry.name);
            } else {
                ImGui.text(fEntry.name);
            }
        }
        ImGui.endChild();
    }

    private void hotkeys() {
        if (!isSomethingHovered) {
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
        inspection = null;
    }

    protected void onClick(FSEntry root) {
        if (root.isHovered && ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
            if (!ImGui.isKeyDown(ImGuiKey.LeftCtrl)) {
                selected.clear();
            }
            if (selected.containsKey(root.getId()) && ImGui.isKeyDown(ImGuiKey.LeftCtrl)) {
                selected.remove(root.getId());
                inspection = null;
            } else {
                selected.put(root.getId(), true);
                if (!root.isDirectory()) {
                    inspection = root;
                }
            }
        }
        if (root.isHovered && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
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
