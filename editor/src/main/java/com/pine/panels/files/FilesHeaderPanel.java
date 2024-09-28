package com.pine.panels.files;

import com.pine.PInject;
import com.pine.repository.Message;
import com.pine.repository.MessageRepository;
import com.pine.repository.MessageSeverity;
import com.pine.service.FSService;
import com.pine.repository.FileInfoDTO;
import com.pine.service.loader.ResourceLoaderService;
import com.pine.service.loader.impl.info.MeshLoaderExtraInfo;
import com.pine.theme.Icons;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import java.io.File;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class FilesHeaderPanel extends AbstractView {
    @PInject
    public FSService fsService;
    @PInject
    public ResourceLoaderService resourceLoader;
    @PInject
    public MessageRepository messageRepository;

    private FilesContext filesContext;
    private final ImString searchPath = new ImString();


    @Override
    public void onInitialize() {
        super.onInitialize();
        filesContext = (FilesContext) getContext();
        searchPath.set(filesContext.getDirectory());
        filesContext.subscribe(() -> {
            searchPath.set(filesContext.getDirectory());
        });
    }

    @Override
    public void renderInternal() {
        if (ImGui.button(Icons.create_new_folder + "##mkdir", ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            fsService.createDirectory(filesContext.getDirectory() + File.separator + "New folder");
        }
        ImGui.sameLine();
        if (ImGui.button(Icons.arrow_upward + "##goUpDir", ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            filesContext.setDirectory(fsService.getParentDir(filesContext.getDirectory()));
        }
        ImGui.sameLine();
        if (ImGui.inputText("##searchPath", searchPath, ImGuiInputTextFlags.EnterReturnsTrue)) {
            if (fsService.exists(searchPath.get())) {
                var context = ((FilesContext) getContext());
                context.setDirectory(searchPath.get());
            }
        }

        FileInfoDTO selected = filesContext.getSelectedFile();
        if (selected != null && !selected.isDirectory()) {
            ImGui.sameLine();
            if (ImGui.button(Icons.file_open + " Import File##importFile")) {
                FileInfoDTO file = filesContext.getSelectedFile();
                if (file != null && !file.isDirectory()) {
                    var response = resourceLoader.load(file.absolutePath(), false, new MeshLoaderExtraInfo().setInstantiateHierarchy(true));
                    if (response == null || !response.isLoaded()) {
                        messageRepository.pushMessage(new Message("Error while importing file {}" + file.absolutePath(), MessageSeverity.ERROR));
                    }
                }
            }
        }
    }
}
