package com.pine.service;

import com.pine.injection.PBean;
import com.pine.repository.streaming.StreamableResourceType;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.nfd.NFDFilterItem;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.lwjgl.util.nfd.NativeFileDialog.*;

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

    private static String openFileDialog() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            String defaultPath = null;

            NFDFilterItem.Buffer filterList = NFDFilterItem.malloc(2, stack);

            StreamableResourceType[] values = StreamableResourceType.values();
            for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
                var type = values[i];
                try (var cur = filterList.get(i)) {
                    cur.name(stack.UTF8(type.name())).spec(stack.UTF8(String.join(",", type.getFileExtensions())));
                }
            }

            PointerBuffer outPath = stack.mallocPointer(1);

            int result = NFD_OpenDialog(outPath, filterList, defaultPath);
            if (result == NFD_OKAY) {
                String selectedPath = outPath.getStringUTF8(0);
                System.out.println("Selected file: " + selectedPath);
                NativeFileDialog.nNFD_FreePath(outPath.get(0));
            }
            return null;
        }
    }
}
