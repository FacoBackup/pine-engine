package com.pine.panels.files;

import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.messaging.Message;
import com.pine.messaging.MessageRepository;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.EditorRepository;
import com.pine.repository.fs.ResourceEntry;
import com.pine.repository.fs.ResourceEntryType;
import com.pine.service.FSService;
import com.pine.service.FilesService;
import com.pine.service.NativeDialogService;
import com.pine.service.loader.LoaderService;
import com.pine.service.loader.impl.info.MeshLoaderExtraInfo;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import java.io.File;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class FilesHeaderPanel extends AbstractView {
    @PInject
    public FSService fsService;
    @PInject
    public LoaderService resourceLoader;
    @PInject
    public MessageRepository messageRepository;
    @PInject
    public NativeDialogService nativeDialogService;
    @PInject
    public FilesService filesService;
    @PInject
    public EditorRepository editorRepository;


    private final ImString searchPath = new ImString();

    private FilesContext context;

    @Override
    public void onInitialize() {
        context = (FilesContext) getContext();
        searchPath.set(context.currentDirectory.path);
        context.subscribe(() -> {
            searchPath.set(context.currentDirectory.path);
        });
    }

    @Override
    public void render() {
        if (ImGui.button(Icons.create_new_folder + "##mkdir", ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            context.currentDirectory.children.add(new ResourceEntry("New Directory (" + context.currentDirectory.children.size() + ")", ResourceEntryType.DIRECTORY, 0, context.currentDirectory.path + File.separator + "New folder", context.currentDirectory, null));
        }
        if (context.currentDirectory.parent != null) {
            ImGui.sameLine();
            if (ImGui.button(Icons.arrow_upward + "##goUpDir", ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                context.setDirectory(context.currentDirectory.parent);
            }
        }
        ImGui.sameLine();
        if (ImGui.inputText("##searchPath", searchPath, ImGuiInputTextFlags.EnterReturnsTrue)) {
            searchFiles();
        }

        ImGui.sameLine();
        if (ImGui.button(Icons.file_open + " Import File##importFile")) {
            importFile();
        }
    }

    private void searchFiles() {
        if (fsService.exists(searchPath.get())) {
            var context = ((FilesContext) getContext());
            ResourceEntry entry = findRecursively(searchPath.get(), editorRepository.rootDirectory);
            if (entry != null && entry.type == ResourceEntryType.DIRECTORY) {
                context.setDirectory(entry);
            } else {
                searchPath.set(context.currentDirectory.path);
                messageRepository.pushMessage("Directory not found", MessageSeverity.ERROR);
            }
        }
    }

    private void importFile() {
        String filePath = nativeDialogService.selectFile();
        if (filePath != null) {
            var response = resourceLoader.load(filePath, new MeshLoaderExtraInfo().setInstantiateHierarchy(true));
            if (response == null || !response.isLoaded) {
                messageRepository.pushMessage(new Message("Error while importing file " + filePath, MessageSeverity.ERROR));
            } else {
                response.loadedResources.forEach(r -> {
                    context.currentDirectory.children.add(new ResourceEntry(r.name, filesService.getType(r.getResourceType()), r.size, r.pathToFile, context.currentDirectory, r));
                });
            }
        }
    }

    private ResourceEntry findRecursively(String search, ResourceEntry entry) {
        if (entry.name.equalsIgnoreCase(search)) {
            return entry;
        }
        for (ResourceEntry c : entry.children) {
            ResourceEntry found = findRecursively(search, c);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
