package com.pine.panels.files;

import com.pine.core.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.messaging.MessageRepository;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.EditorRepository;
import com.pine.repository.FileMetadataRepository;
import com.pine.repository.fs.DirectoryEntry;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.NativeDialogService;
import com.pine.service.ProjectService;
import com.pine.service.importer.ImporterService;
import com.pine.service.importer.metadata.AbstractResourceMetadata;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private AbstractDirectoryPanel directoryPanel;
    private FilesContext context;
    private String searchPath = "";
    private boolean isListView;
    private FileInspectorPanel fileInspector;

    @Override
    public void onInitialize() {
        context = (FilesContext) getContext();
        context.subscribe(this::updateDirectoryPath);
        if (context.currentDirectory == null) {
            context.setDirectory(editorRepository.root);
        }
        updateDirectoryPath();
        appendChild(fileInspector = new FileInspectorPanel());
        switchViewMode();
    }

    private void updateDirectoryPath() {
        StringBuilder path = new StringBuilder();
        DirectoryEntry parent = context.currentDirectory;
        while (parent != null) {
            path.insert(0, parent.name + "/");
            parent = parent.parent;
        }
        searchPath = path.toString();
    }

    private void switchViewMode() {
        if (directoryPanel == null || directoryPanel instanceof CardViewDirectoryPanel) {
            appendChild(directoryPanel = new ListViewDirectoryPanel());
            removeChild(directoryPanel);
            isListView = true;
        } else {
            appendChild(directoryPanel = new CardViewDirectoryPanel());
            removeChild(directoryPanel);
            isListView = false;
        }
    }

    @Override
    public void render() {
        renderHeader();
        directoryPanel.isWindowFocused = isWindowFocused;

        if (context.inspection != null) {

            if (ImGui.beginTable("##files" + imguiId, 2, TABLE_FLAGS)) {
                ImGui.tableSetupColumn("", ImGuiTableColumnFlags.WidthStretch);
                ImGui.tableSetupColumn("", ImGuiTableColumnFlags.WidthFixed, 250f);
                ImGui.tableHeadersRow();

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                directoryPanel.render();
                ImGui.tableNextColumn();
                fileInspector.render();

                ImGui.endTable();
            }
        } else {
            directoryPanel.render();
        }
    }

    private void renderHeader() {
        if (ImGui.button(Icons.create_new_folder + "##mkdir", ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            String part = "";
            if (!context.currentDirectory.directories.isEmpty()) {
                part = " (" + context.currentDirectory.directories.size() + ")";
            }
            var d = new DirectoryEntry("New Directory" + part, context.currentDirectory);
            context.currentDirectory.directories.put(d.id, d);
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
        ImGui.dummy(ImGui.getContentRegionAvailX() - 150, 0);

        ImGui.sameLine();
        if (renderWithHighlight(Icons.dashboard + "##cardView", !isListView)) {
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
            Map<StreamableResourceType, List<AbstractResourceMetadata>> byType = new HashMap<>();
            for (var data : response) {
                byType.putIfAbsent(data.getResourceType(), new ArrayList<>());
                byType.get(data.getResourceType()).add(data);
            }
            if (byType.size() > 1) {
                for (var entry : byType.entrySet()) {
                    var k = entry.getKey();
                    var d = new DirectoryEntry("Imported - " + k.getTitle(), context.currentDirectory);
                    context.currentDirectory.directories.put(d.id, d);
                    for (var f : entry.getValue()) {
                        d.files.add(f.id);
                    }
                }
            } else {
                context.currentDirectory.files.addAll(response.stream().map(a -> a.id).collect(Collectors.toSet()));
            }
            fileMetadataRepository.refresh();
            messageRepository.pushMessage(paths.size() + " files imported", MessageSeverity.SUCCESS);
            projectService.saveSilently();
        });
    }
}

