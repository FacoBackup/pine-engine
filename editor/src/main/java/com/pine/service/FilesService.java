package com.pine.service;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.fs.FileEntry;
import com.pine.repository.fs.IEntry;
import com.pine.repository.streaming.StreamingRepository;

import java.util.Map;

@PBean
public class FilesService {

    @PInject
    public Engine engine;

    @PInject
    public ProjectService projectService;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public StreamingRepository streamingRepository;

    public void delete(Map<String, IEntry> toDelete) {
//        editorRepository.inspectFile = selected == editorRepository.inspectFile ? null : editorRepository.inspectFile;
//        if (selected != editorRepository.rootDirectory) {
//            selected.streamableResource.invalidated = true;
//            selected.parent.children.remove(selected);
//            if (selected.type != FileType.DIRECTORY) {
////                new File(engine.getResourceTargetDirectory() + selected.streamableResource.pathToFile).delete();
//            }
//            projectService.saveSilently();
//        }
    }
}
