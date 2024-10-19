package com.pine.panels.files;

import com.pine.core.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.messaging.MessageRepository;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.EditorRepository;
import com.pine.repository.FileMetadataRepository;
import com.pine.repository.fs.DirectoryEntry;
import com.pine.service.NativeDialogService;
import com.pine.service.ProjectService;
import com.pine.service.importer.ImporterService;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;

import java.util.List;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class FilesPanel extends AbstractDockPanel {
    private static final int TABLE_FLAGS = ImGuiTableFlags.Resizable | ImGuiTableFlags.NoBordersInBody;

    @PInject
    public ImporterService resourceLoader;
    @PInject
    public MessageRepository messageRepository;
    @PInject
    public NativeDialogService nativeDialogService;
    @PInject
    public EditorRepository editorRepository;
    @PInject
    public ProjectService projectService;
    @PInject
    public FileMetadataRepository fileMetadataRepository;

    private AbstractDirectoryPanel directory;
    private FilesContext context;
    private String searchPath = "";
    private boolean isListView;
    private FileInspectorPanel fileInspector;

    @Override
    public void onInitialize() {
        context = (FilesContext) getContext();
        context.subscribe(() -> {
            StringBuilder path = new StringBuilder();
            DirectoryEntry parent = context.currentDirectory;
            while (parent != null) {
                path.insert(0, parent.name + "/");
                parent = parent.parent;
            }
            searchPath = path.toString();
        });
        if (context.currentDirectory == null) {
            context.setDirectory(editorRepository.root);
        }

        appendChild(fileInspector = new FileInspectorPanel());
        switchViewMode();
    }

    private void switchViewMode() {
        if (directory == null || directory instanceof CardViewDirectoryPanel) {
            appendChild(directory = new ListViewDirectoryPanel());
            removeChild(directory);
            isListView = true;
        } else {
            appendChild(directory = new CardViewDirectoryPanel());
            removeChild(directory);
            isListView = false;
        }
    }

    @Override
    public void render() {
        renderHeader();
        directory.isWindowFocused = isWindowFocused;

        if (ImGui.beginTable("##files" + imguiId, 2, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Inspect file", ImGuiTableColumnFlags.WidthFixed, 250f);
            ImGui.tableHeadersRow();

            ImGui.tableNextRow();
            ImGui.tableNextColumn();
            directory.render();
            ImGui.tableNextColumn();
            fileInspector.render();

            ImGui.endTable();
        }

    }

    private void renderHeader() {
        if (ImGui.button(Icons.create_new_folder + "##mkdir", ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            String part = "";
            if (!context.currentDirectory.directories.isEmpty()) {
                part = " (" + context.currentDirectory.directories.size() + ")";
            }
            context.currentDirectory.directories.add(new DirectoryEntry("New Directory" + part, context.currentDirectory));
        }
        if (context.currentDirectory.parent != null) {
            ImGui.sameLine();
            if (ImGui.button(Icons.arrow_upward + "##goUpDir", ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                context.setDirectory(context.currentDirectory.parent);
            }
        }
        ImGui.sameLine();
        ImGui.text(searchPath);

        ImGui.sameLine();
        if (renderWithHighlight(Icons.grid_4x4 + "##cardView", !isListView)) {
            switchViewMode();
        }

        ImGui.sameLine();
        if (renderWithHighlight(Icons.list + "##listView", isListView)) {
            switchViewMode();
        }

        ImGui.sameLine();
        if (ImGui.button(Icons.file_open + " Import File##importFile")) {
            importFile();
        }
    }

    private boolean renderWithHighlight(String label, boolean highlight) {
        int popStyle = 0;
        if (highlight) {
            ImGui.pushStyleColor(ImGuiCol.Button, editorRepository.accent);
            popStyle++;
        }

        if (ImGui.button(label, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            ImGui.popStyleColor(popStyle);
            return true;
        }
        ImGui.popStyleColor(popStyle);
        return false;
    }

    private void importFile() {
        List<String> paths = nativeDialogService.selectFile();
        if (paths.isEmpty()) {
            return;
        }
        resourceLoader.importFiles(paths, response -> {
            if (response.isEmpty()) {
                messageRepository.pushMessage("Could not import files: " + paths, MessageSeverity.ERROR);
            }
            context.currentDirectory.files.addAll(response);
            projectService.saveSilently();
            fileMetadataRepository.refresh();
        });
    }
}

