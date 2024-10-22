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
    private FileInspectorPanel fileInspector;
    private boolean isFirstRender;

    @Override
    public void onInitialize() {
        context = (FilesContext) getContext();
        context.subscribe(this::updateDirectoryPath);
        if (context.currentDirectory == null) {
            context.setDirectory(editorRepository.root);
        }
        updateDirectoryPath();
        appendChild(fileInspector = new FileInspectorPanel());

        appendChild(directoryPanel = new ListViewDirectoryPanel());
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

    @Override
    public void render() {
        renderHeader();
        directoryPanel.isWindowFocused = isWindowFocused;

        ImGui.columns(2, "##filesColumns" + imguiId);

        if(!isFirstRender){
            isFirstRender = true;
            ImGui.setColumnWidth(0, size.x  * .75f);
        }

        directoryPanel.render();
        ImGui.nextColumn();
        if (context.inspection != null) {
            fileInspector.render();
        }
        ImGui.columns(1);
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
        ImGui.dummy(ImGui.getContentRegionAvailX() - 100, 0);

        ImGui.sameLine();
        if (ImGui.button(Icons.file_open + " Import File##importFile")) {
            importFile();
        }
        ImGui.separator();
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
            var index = context.currentDirectory.directories.size();
            if (byType.size() > 1) {
                for (var entry : byType.entrySet()) {
                    var k = entry.getKey();
                    var d = new DirectoryEntry("(" + index + ") imported - " + k.getTitle(), context.currentDirectory);
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

