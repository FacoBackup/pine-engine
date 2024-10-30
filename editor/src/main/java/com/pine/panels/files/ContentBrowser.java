package com.pine.panels.files;

import com.pine.core.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.messaging.MessageRepository;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.FilesRepository;
import com.pine.repository.FSEntry;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.FilesService;
import com.pine.service.importer.ImporterService;
import com.pine.theme.Icons;
import imgui.ImGui;

import java.util.*;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class ContentBrowser extends AbstractDockPanel {
    @PInject
    public ImporterService importerService;
    @PInject
    public MessageRepository messageRepository;
    @PInject
    public FilesRepository filesRepository;
    @PInject
    public FilesService filesService;

    private DirectoryPanel directoryPanel;
    private String searchPath = "";
    private FileInspectorPanel fileInspector;
    private boolean isFirstRender;

    private String currentDirectory = FilesRepository.ROOT_DIRECTORY_ID;
    private FSEntry inspection;
    private final Map<String, Boolean> selected = new HashMap<>();
    private final Map<String, Boolean> toCut = new HashMap<>();

    @Override
    public void onInitialize() {
        updateDirectoryPath();
        appendChild(fileInspector = new FileInspectorPanel());
        appendChild(directoryPanel = new DirectoryPanel());
    }

    private void updateDirectoryPath() {
        StringBuilder path = new StringBuilder();
        FSEntry parent =  filesRepository.entry.get(currentDirectory);
        while (parent != null) {
            path.insert(0, parent.name + "/");
            String parentId = filesRepository.childParent.get(parent.id);
            parent = parentId == null ? null :  filesRepository.entry.get(parentId);
        }
        searchPath = path.toString();
    }

    @Override
    public void render() {
        if(filesRepository.isImporting){
            return;
        }

        fileInspector.setInspection(inspection);
        directoryPanel.selected = selected;
        directoryPanel.toCut = toCut;
        directoryPanel.currentDirectory = currentDirectory;

        renderHeader();
        directoryPanel.isWindowFocused = isWindowFocused;
        ImGui.columns(2, "##filesColumns" + imguiId);
        if (!isFirstRender) {
            isFirstRender = true;
            ImGui.setColumnWidth(0, size.x * .75f);
        }

        directoryPanel.render();
        inspection = directoryPanel.inspection;
        if(!Objects.equals(currentDirectory, directoryPanel.currentDirectory)){
            updateDirectoryPath();
            currentDirectory = directoryPanel.currentDirectory;
        }

        ImGui.nextColumn();
        if (inspection != null) {
            fileInspector.render();
        }
        ImGui.columns(1);
    }

    private void renderHeader() {
        if (ImGui.button(Icons.create_new_folder + "##mkdir", ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            String id = UUID.randomUUID().toString();
            var d = new FSEntry("New Directory (" + id.substring(0, 4) + ")", id);
            filesRepository.childParent.put(id, currentDirectory);
            filesRepository.parentChildren.get(id).add(id);
            filesRepository.parentChildren.put(id, new ArrayList<>());
            filesRepository.entry.put(id, d);
        }
        if (!Objects.equals(currentDirectory, FilesRepository.ROOT_DIRECTORY_ID)) {
            ImGui.sameLine();
            if (ImGui.button(Icons.arrow_upward + "##goUpDir", ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                currentDirectory = filesRepository.childParent.get(currentDirectory);
            }
        }
        ImGui.sameLine();
        ImGui.text(searchPath);

        ImGui.sameLine();
        ImGui.dummy(ImGui.getContentRegionAvailX() - 210, 0);

        ImGui.sameLine();
        if (ImGui.button(StreamableResourceType.MATERIAL.getIcon() + "Create material##addFile" + imguiId)) {
            var response = importerService.createNew(StreamableResourceType.MATERIAL);
            if (response == null) {
                messageRepository.pushMessage("Could not create new file", MessageSeverity.ERROR);
            } else {
                filesService.createEntry(currentDirectory, response);
            }
        }

        ImGui.sameLine();
        if (ImGui.button(Icons.file_open + "Import File##importFile" + imguiId)) {
            importFile();
        }
        ImGui.separator();
    }

    private void importFile() {
        filesService.importFile(currentDirectory);
    }
}

