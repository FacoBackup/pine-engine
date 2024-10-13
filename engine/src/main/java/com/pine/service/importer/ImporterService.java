package com.pine.service.importer;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.streaming.AbstractStreamableResource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Responsible for parsing data and storing it locally while registering the StreamableResource on StreamableResourceRepository
 */
@PBean
public class ImporterService implements Loggable {

    @PInject
    public List<AbstractImporter> resourceLoaders;

    public void importFiles(List<String> paths, Consumer<List<AbstractStreamableResource<?>>> callback) {
        new Thread(() -> {
            List<AbstractStreamableResource<?>> imported = new ArrayList<>();
            for (String path : paths) {
                if (path == null) {
                    continue;
                }
                final String extension = path.substring(path.lastIndexOf(".") + 1);
                for (AbstractImporter i : resourceLoaders) {
                    if (i.getResourceType().getFileExtensions().contains(extension)) {
                        imported.addAll(i.load(path));
                    }
                }
            }
            callback.accept(imported);
        }).start();
    }
}
