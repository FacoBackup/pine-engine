package com.pine.panels.painting;

import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.panels.component.impl.ResourceField;
import com.pine.repository.EditorRepository;
import com.pine.repository.FSEntry;
import com.pine.repository.FilesRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.theme.Icons;
import imgui.ImGui;

import java.util.HashMap;
import java.util.Map;

public class FoliagePanel extends AbstractView {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public FilesRepository filesRepository;

    private final Map<String, Boolean> toRemove = new HashMap<>();

    @Override
    public void onInitialize() {

    }

    @Override
    public void render() {
        ImGui.dummy(0, 8);
        ImGui.text(Icons.add + "Foliage");
        if(ImGui.beginChild(imguiId, ImGui.getWindowSizeX(), ImGui.getWindowSizeY() * .35f, true)){
            for (String m : filesRepository.byType.get(StreamableResourceType.MESH)) {
                if(editorRepository.selectedFoliage.containsKey(m)){
                    continue;
                }

                FSEntry entry = filesRepository.entry.get(m);
                if (ImGui.button(entry.name)) {
                    editorRepository.selectedFoliage.put(m, true);
                }
            }
        }
        ImGui.endChild();

        ImGui.dummy(0, 8);
        ImGui.text(Icons.check_box + "Selected");
        if(ImGui.beginChild(imguiId + "selected", ImGui.getWindowSizeX(), ImGui.getWindowSizeY() * .35f, true)){
            for (String m : editorRepository.selectedFoliage.keySet()) {
                FSEntry entry = filesRepository.entry.get(m);

                if(entry == null){
                    toRemove.put(m, true);
                    continue;
                }

                if (ImGui.button(entry.name+ "##remove")) {
                    toRemove.put(m, true);
                }
            }

            if(!toRemove.isEmpty()){
                toRemove.keySet().forEach(e -> {
                    editorRepository.selectedFoliage.remove(e);
                });
                toRemove.clear();
            }
        }
        ImGui.endChild();
    }
}
