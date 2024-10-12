package com.pine.service;

import com.pine.injection.PBean;
import com.pine.repository.streaming.StreamableResourceType;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NFDFilterItem;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

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

    public List<String> selectFile() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            List<String> selectedPath = new ArrayList<>();

            List<String> fileTypes = new ArrayList<>();
            for (StreamableResourceType type : StreamableResourceType.values()) {
                fileTypes.addAll(type.getFileExtensions());
            }

            NFDFilterItem.Buffer filterList = NFDFilterItem.malloc(1, stack);
            filterList.get(0).name(stack.UTF8("Assets")).spec(stack.UTF8(String.join(",", fileTypes)));

            PointerBuffer outPaths = stack.mallocPointer(2);
            int result = NFD_OpenDialogMultiple(outPaths, filterList, (CharSequence) null);
            if (result == NFD_OKAY) {
                long outPathPointer = outPaths.get(0);

                IntBuffer al = MemoryUtil.memAllocInt(1);
                NativeFileDialog.NFD_PathSet_GetCount(outPathPointer, al);
                for (int i = 0; i < al.get(0); i++) {
                    PointerBuffer outPath = stack.mallocPointer(1);
                    if (NativeFileDialog.NFD_PathSet_GetPath(outPathPointer, i, outPath) == NFD_OKAY) {
                        selectedPath.add(outPath.getStringUTF8(0));
                    }
                    NativeFileDialog.nNFD_FreePath(outPath.get(0));
                }
                MemoryUtil.memFree(al);
//                MemoryUtil.memFree(outPaths);
            }
            return selectedPath;
        }
    }
}
