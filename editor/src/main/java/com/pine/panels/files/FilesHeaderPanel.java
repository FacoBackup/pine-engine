package com.pine.panels.files;

import com.pine.Icon;
import com.pine.PInject;
import com.pine.common.fs.FSService;
import com.pine.common.fs.FileInfoDTO;
import com.pine.service.loader.ResourceLoaderService;
import com.pine.service.loader.impl.info.MeshLoaderExtraInfo;
import com.pine.ui.view.AbstractView;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import java.io.File;

public class FilesHeaderPanel extends AbstractView {
    @PInject
    public FSService fsService;
    @PInject
    public ResourceLoaderService resourceLoader;

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
        if(ImGui.button(Icon.FOLDERPLUS.codePoint + "##mkdir")){
            fsService.createDirectory(filesContext.getDirectory() + File.separator + "New folder");
        }
        ImGui.sameLine();
        if(ImGui.button(Icon.ARROWUP.codePoint + "##goUpDir")){
            filesContext.setDirectory(fsService.getParentDir(filesContext.getDirectory()));
        }
        ImGui.sameLine();
        if(ImGui.inputText("##searchPath", searchPath, ImGuiInputTextFlags.EnterReturnsTrue)){
            if (fsService.exists(searchPath.get())) {
                var context = ((FilesContext) getContext());
                context.setDirectory(searchPath.get());
            }
        }

        FileInfoDTO selected = filesContext.getSelectedFile();
        if(selected != null && !selected.isDirectory()) {
            ImGui.sameLine();
            if (ImGui.button(Icon.FILE.codePoint + " Import File##importFile")) {
                FileInfoDTO file = filesContext.getSelectedFile();
                if (file != null && !file.isDirectory()) {
                    resourceLoader.load(file.absolutePath(), false, new MeshLoaderExtraInfo().setInstantiateHierarchy(true));
                }
            }
        }
    }
}
