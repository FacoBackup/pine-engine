package com.pine.app.panels.inspector;

import com.pine.InjectBean;
import com.pine.app.EditorWindow;
import com.pine.app.component.FormPanel;
import com.pine.app.repository.EntitySelectionRepository;
import com.pine.inspection.WithMutableData;
import com.pine.service.world.WorldService;
import com.pine.ui.panel.AbstractWindowPanel;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class InspectorPanel extends AbstractWindowPanel {
    @InjectBean
    public EntitySelectionRepository selectionRepository;
    // INJECT METADATA COMPONENT

    // INJECT ALL COMPONENT TYPES TO GET COMPONENT LABELS

    private Integer selected;
    private final List<FormPanel> formPanels = new LinkedList<>();
    private WorldService worldService;

    @Override
    protected String getTitle() {
        return "Inspector";
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        worldService = ((EditorWindow) document.getWindow()).getEngine().getWorld();
    }

    @Override
    public void tick() {
        Integer first = selectionRepository.getMainSelection();
        if (!Objects.equals(first, selected)) {
            selected = first;
            formPanels.forEach(this::removeChild);
            if (selected != null) {
                for (var component : worldService.getComponents(selected).values()) {
                    FormPanel formPanel;
                    formPanels.add(formPanel = new FormPanel((WithMutableData) component, (d, dd) -> {

                    }));
                    appendChild(formPanel);
                }
            }
        }
        super.tick();
    }

    @Override
    public void renderInternal() {
        // PARENT SELECTION
        // COMPONENT CREATION
//        if(ImGui.combo(Icon.PLUS.codePoint + " Add component", null, ))
        super.renderInternal();
    }
}
