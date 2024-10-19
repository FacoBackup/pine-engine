package com.pine.panels.files;

import com.pine.repository.fs.DirectoryEntry;
import com.pine.repository.fs.FileEntry;
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
            for (var child : context.currentDirectory.directories) {
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

        ImGui.tableNextColumn();
        ImGui.text(root.name);
        onClick(root);

        ImGui.tableNextColumn();
        ImGui.text("--");
        onClick(root);

        ImGui.tableNextColumn();
        ImGui.text("Directory");
        onClick(root);

        ImGui.tableNextColumn();
        ImGui.text("--");
        onClick(root);
    }

    private void renderFile(FileEntry root) {
        StreamableResourceType resourceType = root.metadata.getResourceType();
        ImGui.tableNextRow();
        if (context.selected.containsKey(root.getId())) {
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg0, editorRepository.accentU32);
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg1, editorRepository.accentU32);
        }
        ImGui.tableNextColumn();
        ImGui.text(resourceType.getIcon());
        onClick(root);

        ImGui.tableNextColumn();
        ImGui.text(root.metadata.name);
        onClick(root);

        ImGui.tableNextColumn();
        ImGui.text(root.creationDateString);
        onClick(root);

        ImGui.tableNextColumn();
        ImGui.text(resourceType.getTitle());
        onClick(root);

        ImGui.tableNextColumn();
        ImGui.text(root.sizeText);
        onClick(root);
    }
}