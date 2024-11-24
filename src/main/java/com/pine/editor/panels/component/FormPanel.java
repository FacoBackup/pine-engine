package com.pine.editor.panels.component;

import com.pine.common.injection.PInject;
import com.pine.common.inspection.FieldDTO;
import com.pine.common.inspection.Inspectable;
import com.pine.common.inspection.MethodDTO;
import com.pine.editor.core.AbstractView;
import com.pine.editor.panels.component.impl.*;
import com.pine.editor.repository.EditorRepository;
import com.pine.editor.service.ThemeService;
import com.pine.engine.inspection.ResourceTypeField;
import com.pine.engine.inspection.TypePreviewField;
import imgui.ImGui;
import imgui.flag.ImGuiCol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class FormPanel extends AbstractView {
    private final BiConsumer<FieldDTO, Object> changeHandler;
    private Inspectable inspectable;
    private final Map<String, AccordionPanel> groups = new HashMap<>();
    private String search;
    private boolean compactMode;
    private boolean somethingMatches;

    @PInject
    public ThemeService theme;

    public FormPanel(BiConsumer<FieldDTO, Object> changeHandler) {
        this.changeHandler = changeHandler;
    }

    public void setInspection(Inspectable data) {
        if (this.inspectable == data) {
            return;
        }
        this.inspectable = data;
        children.clear();
        groups.clear();

        if (data == null) {
            return;
        }
        processMethods(data);
        processFields(data);
    }

    private void processMethods(Inspectable data) {
        for (MethodDTO methodDTO : data.getMethodsAnnotated()) {
            if (!groups.containsKey(methodDTO.getGroup())) {
                groups.put(methodDTO.getGroup(), appendChild(new AccordionPanel()));
            }

            AccordionPanel group = groups.get(methodDTO.getGroup());
            group.title = methodDTO.getGroup();
            group.append(new ExecutableFunctionField(methodDTO));
        }
    }

    private void processFields(Inspectable data) {
        for (FieldDTO field : data.getFieldsAnnotated()) {
            if (!groups.containsKey(field.getGroup())) {
                groups.put(field.getGroup(), appendChild(new AccordionPanel()));
            }

            AccordionPanel group = groups.get(field.getGroup());
            group.title = field.getGroup();
            switch (field.getType()) {
                case STRING:
                    if (field.getField().isAnnotationPresent(ResourceTypeField.class)) {
                        group.append(new ResourceField(field, changeHandler));
                    } else if (field.getField().isAnnotationPresent(TypePreviewField.class)) {
                        group.append(new PreviewField(field, changeHandler));
                    } else {
                        group.append(new StringField(field, changeHandler));
                    }
                    break;
                case INT:
                    group.append(new IntField(field, changeHandler));
                    break;
                case FLOAT:
                    group.append(new FloatField(field, changeHandler));
                    break;
                case BOOLEAN:
                    group.append(new BooleanField(field, changeHandler));
                    break;
                case VECTOR2:
                    group.append(new Vector2Field(field, changeHandler));
                    break;
                case VECTOR3:
                    group.append(new Vector3Field(field, changeHandler));
                    break;
                case VECTOR4:
                    group.append(new Vector4Field(field, changeHandler));
                    break;
                case QUATERNION:
                    group.append(new QuaternionField(field, changeHandler));
                    break;
                case COLOR:
                    group.append(new ColorField(field, changeHandler));
                    break;
                case OPTIONS:
                    group.append(new OptionsField(field, changeHandler));
                    break;
            }
        }
    }

    public Inspectable getInspectable() {
        return inspectable;
    }

    @Override
    public void render() {
        if (inspectable != null) {
            ImGui.pushStyleColor(ImGuiCol.Header, theme.neutralPalette);
            if (search == null || search.isEmpty()) {
                if (compactMode) {
                    if (ImGui.collapsingHeader(inspectable.getIcon() + inspectable.getTitle() + imguiId)) {
                        super.render();
                        ImGui.separator();
                    }
                } else {
                    renderTitle();
                    super.render();
                }
            } else {
                if (somethingMatches) {
                    renderTitle();
                }
                somethingMatches = false;
                for (var group : groups.values()) {
                    boolean groupMatches = group.title.toLowerCase().contains(search);
                    if (groupMatches) {
                        ImGui.text(group.title);
                        ImGui.separator();
                    }
                    for (var view : group.getViews()) {
                        if (groupMatches || view.getName().toLowerCase().contains(search)) {
                            view.render();
                            somethingMatches = true;
                        }
                    }
                }
            }
            ImGui.popStyleColor();
        }
    }

    private void renderTitle() {
        ImGui.text(inspectable.getIcon() + inspectable.getTitle());
        ImGui.separator();
    }

    public void setSearch(String fieldSearch) {
        this.search = fieldSearch;
    }

    public void setCompactMode(boolean v) {
        this.compactMode = v;
    }
}
