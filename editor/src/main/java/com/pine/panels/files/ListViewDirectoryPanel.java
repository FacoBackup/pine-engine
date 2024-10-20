package com.pine.panels.files;

import com.pine.repository.fs.DirectoryEntry;
import com.pine.repository.fs.FileEntry;
import com.pine.repository.fs.IEntry;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.flag.ImGuiTableBgTarget;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;

public class ListViewDirectoryPanel extends AbstractDirectoryPanel {
    private static final int FLAGS = ImGuiTableFlags.ScrollY | ImGuiTableFlags.RowBg;

    @Override
    public void render() {
        updateFiles();
        hotkeys();

        if (ImGui.beginTable(imguiId, 5, FLAGS)) {
            ImGui.tableSetupColumn("", ImGuiTableColumnFlags.WidthFixed, 30f);
            ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Import date", ImGuiTableColumnFlags.WidthFixed, 100f);
            ImGui.tableSetupColumn("Type", ImGuiTableColumnFlags.WidthFixed, 100f);
            ImGui.tableSetupColumn("Size", ImGuiTableColumnFlags.WidthFixed, 100f);
            ImGui.tableHeadersRow();
            for (var child : context.currentDirectory.directories.values()) {
                renderDirectory(child);
            }
            for (var child : filesLocal) {
                renderFile(child);
            }
            ImGui.endTable();
        }
    }

    private void renderDirectory(DirectoryEntry root) {
        ImGui.tableNextRow();
        if (context.selected.containsKey(root.getId())) {
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg0, editorRepository.accentU32);
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg1, editorRepository.accentU32);
        }

        ImGui.tableNextColumn();
        ImGui.textColored(DIRECTORY_COLOR, Icons.folder);
        onClick(root);
        if (context.toCut.containsKey(root.id)) {
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
        StreamableResourceType resourceType = root.metadata.getResourceType();
        ImGui.tableNextRow();
        if (context.selected.containsKey(root.getId())) {
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg0, editorRepository.accentU32);
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg1, editorRepository.accentU32);
        }
        if (context.toCut.containsKey(root.getId())) {
            textDisabledColumn(resourceType.getIcon(), root);
            textDisabledColumn(root.metadata.name, root);
            textDisabledColumn(root.creationDateString, root);
            textDisabledColumn(resourceType.getTitle(), root);
            textDisabledColumn(root.sizeText, root);
        } else {
            textColumn(resourceType.getIcon(), root);
            textColumn(root.metadata.name, root);
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
}