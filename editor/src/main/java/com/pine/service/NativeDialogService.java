package com.pine.service;

import com.pine.injection.PBean;
import com.pine.repository.streaming.StreamableResourceType;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.nfd.NFDFilterItem;
import org.lwjgl.util.nfd.NativeFileDialog;

import static org.lwjgl.util.nfd.NativeFileDialog.NFD_OKAY;
import static org.lwjgl.util.nfd.NativeFileDialog.NFD_OpenDialog;

@PBean
public class NativeDialogService {
    public String selectDirectory() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            NativeFileDialog.NFD_Init();
            String selectedDirectory = openDirectoryDialog();
            NativeFileDialog.NFD_Quit();
            return selectedDirectory;
        }
    }

    private static String openDirectoryDialog() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer outPath = stack.mallocPointer(1);
            int result = NativeFileDialog.NFD_PickFolder(outPath, System.getProperty("user.home"));
            if (result == NFD_OKAY) {
                String path = outPath.getStringUTF8(0);
                NativeFileDialog.nNFD_FreePath(outPath.get(0));
                return path;
            }
        }
        return null;
    }

    public String selectFile() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            String defaultPath = null;
            String selectedPath = null;

            StreamableResourceType[] values = StreamableResourceType.values();
            NFDFilterItem.Buffer filterList = NFDFilterItem.malloc(values.length, stack);
            for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
                var type = values[i];
                filterList.get(i).name(stack.UTF8(type.name())).spec(stack.UTF8(String.join(",", type.getFileExtensions())));
            }

            PointerBuffer outPath = stack.mallocPointer(1);

            int result = NFD_OpenDialog(outPath, filterList, defaultPath);
            if (result == NFD_OKAY) {
                selectedPath = outPath.getStringUTF8(0);
                NativeFileDialog.nNFD_FreePath(outPath.get(0));
            }
            return selectedPath;
        }
    }
}
