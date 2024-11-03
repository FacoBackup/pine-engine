package com.pine.panels.painting;

import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.panels.component.impl.ResourceField;
import com.pine.repository.*;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.ImVec2;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static com.pine.panels.viewport.ViewportPanel.INV_X;
import static com.pine.panels.viewport.ViewportPanel.INV_Y;

public class FoliagePanel extends AbstractView {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public FilesRepository filesRepository;

    @PInject
    public StreamingService streamingService;

    @PInject
    public TerrainRepository terrainRepository;

    private final ImVec2 maskRes = new ImVec2();
    private boolean showMask = false;
    private final Map<String, Boolean> toRemove = new HashMap<>();

    @Override
    public void onInitialize() {

    }

    @Override
    public void render() {
        ImGui.dummy(0, 8);
        if (ImGui.checkbox("Show mask" + imguiId, showMask)) {
            showMask = !showMask;
        }

        if (showMask) {
            var targetTexture = (TextureResourceRef) streamingService.stream(terrainRepository.instanceMaskMap, StreamableResourceType.TEXTURE);
            if (targetTexture != null) {
                ImGui.setNextWindowSize(150, 150);
                if (ImGui.beginChild("image##v")) {
                    maskRes.x = ImGui.getWindowSizeX();
                    maskRes.y = ImGui.getWindowSizeX();
                    ImGui.image(targetTexture.texture, maskRes, INV_Y, INV_X);
                }
                ImGui.endChild();
            }
        }

        ImGui.text(Icons.add + "Foliage");
        if (ImGui.beginChild(imguiId, ImGui.getWindowSizeX(), ImGui.getWindowSizeY() * .35f, true)) {
            for (String m : filesRepository.byType.get(StreamableResourceType.MESH)) {
                if (terrainRepository.selectedFoliage.containsKey(m)) {
                    continue;
                }

                FSEntry entry = filesRepository.entry.get(m);
                if (ImGui.button(entry.name)) {
                    var instance = new FoliageInstance(m);
                    terrainRepository.selectedFoliage.put(m, instance);
                    instance.index = terrainRepository.selectedFoliage.size() - 1;
                }
            }
        }
        ImGui.endChild();

        ImGui.dummy(0, 8);
        ImGui.text(Icons.check_box + "Selected");
        if (ImGui.beginChild(imguiId + "selected", ImGui.getWindowSizeX(), ImGui.getWindowSizeY() * .35f, true)) {
            for (FoliageInstance m : terrainRepository.selectedFoliage.values()) {
                FSEntry entry = filesRepository.entry.get(m.id);

                if (entry == null) {
                    toRemove.put(m.id, true);
                    continue;
                }

                ImGui.text(Icons.forest + entry.name);
                ImGui.sameLine();
                if (ImGui.button(Icons.remove + "remove##" + m)) {
                    toRemove.put(m.id, true);
                }
            }

            if (!toRemove.isEmpty()) {
                toRemove.keySet().forEach(e -> {
                    terrainRepository.selectedFoliage.remove(e);
                });
                toRemove.clear();
                int i = 0;
                for (FoliageInstance f : terrainRepository.selectedFoliage.values()) {
                    f.index = i;
                    i++;
                }
            }
        }
        ImGui.endChild();
    }
}
