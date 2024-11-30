package com.pine.editor.panels.component.impl;

import com.pine.common.Icons;
import com.pine.common.inspection.FieldDTO;
import com.pine.common.inspection.Inspectable;
import com.pine.editor.core.UIUtil;
import com.pine.editor.panels.component.AbstractFormField;
import com.pine.editor.panels.component.FormPanel;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ListField extends AbstractFormField {
    private final List<Inspectable> value;
    private final List<FormPanel> forms = new ArrayList<>();

    public ListField(FieldDTO field, BiConsumer<FieldDTO, Object> changeHandler) {
        super(field, changeHandler);
        value = (List<Inspectable>) field.getValue();
    }

    @Override
    public void render() {
        if(ImGui.collapsingHeader(dto.getLabel(), ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.indent(UIUtil.IDENT);
            if (ImGui.button(Icons.add + "Add new")) {
                try {
                    value.add((Inspectable) dto.getClassType().getConstructor().newInstance());
                } catch (Exception e) {
                    getLogger().error("Could not instantiate object", e);
                }
            }

            for (int i = 0; i < value.size(); i++) {
                var instance = value.get(i);
                if (forms.size() - 1 < i) {
                    var form = appendChild(new FormPanel(changeHandler));
                    forms.add(form);
                    removeChild(form);
                    form.setInspection(instance);
                    form.setCompactMode(true);
                    form.setDefaultOpen(true);
                }
                forms.get(i).render();
            }
            ImGui.unindent(UIUtil.IDENT);
        }
    }
}
