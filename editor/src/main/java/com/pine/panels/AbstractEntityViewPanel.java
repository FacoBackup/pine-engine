package com.pine.panels;

import com.pine.core.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.messaging.MessageRepository;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.EditorRepository;
import com.pine.service.SelectionService;
import com.pine.service.grid.HashGridService;
import com.pine.service.rendering.RequestProcessingService;
import com.pine.service.request.CopyEntitiesRequest;
import com.pine.service.request.DeleteEntityRequest;
import imgui.ImGui;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.flag.ImGuiKey;

public abstract class AbstractEntityViewPanel extends AbstractDockPanel {
    @PInject
    public SelectionService selectionService;
    @PInject
    public HashGridService hashGridService;
    @PInject
    public EditorRepository stateRepository;
    @PInject
    public RequestProcessingService requestProcessingService;
    @PInject
    public MessageRepository messageRepository;

    protected void hotKeys() {
        if(!isWindowFocused || ImGuizmo.isUsing()){
            return;
        }
        var isNotEmptyOfSelection = !stateRepository.selected.isEmpty();
        boolean ctrlDown = ImGui.isKeyDown(ImGuiKey.LeftCtrl);

        if (isNotEmptyOfSelection && ImGui.isKeyPressed(ImGuiKey.Delete)) {
            messageRepository.pushMessage("Deleting selected", MessageSeverity.WARN);
            requestProcessingService.addRequest(new DeleteEntityRequest(stateRepository.selected.keySet()));
            stateRepository.selected.keySet().forEach(s -> stateRepository.pinnedEntities.remove(s));
            stateRepository.selected.clear();
            stateRepository.mainSelection = null;
            stateRepository.primitiveSelected = null;
        }

        if (isNotEmptyOfSelection && ImGui.isKeyPressed(ImGuiKey.C)) {
            messageRepository.pushMessage("Copying "  + stateRepository.selected.size() + " entities", MessageSeverity.WARN);
            stateRepository.copied.clear();
            stateRepository.copied.addAll(stateRepository.selected.keySet());
        }

        if (ctrlDown && ImGui.isKeyPressed(ImGuiKey.A)) {
            for(var tile : hashGridService.getLoadedTiles()){
                if(tile != null) {
                    selectionService.addAllSelected(tile.getWorld().entityMap.values());
                }
            }
        }

        if (!stateRepository.copied.isEmpty() && ctrlDown && ImGui.isKeyPressed(ImGuiKey.V)) {
            messageRepository.pushMessage("Pasting "  + stateRepository.copied.size() + " entities", MessageSeverity.WARN);
            var request = new CopyEntitiesRequest(stateRepository.copied, selectionService.stateRepository.mainSelection);
            requestProcessingService.addRequest(request);
            selectionService.clearSelection();
            selectionService.addAllSelected(request.getAllCloned());
        }

        hotKeysInternal();
    }

    protected void hotKeysInternal(){
    }
}
