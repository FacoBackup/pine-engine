package com.pine;

import com.pine.app.EditorWindow;
import com.pine.service.WindowService;
import org.apache.commons.io.input.Tailer;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;


public class PineEngine {
    public static void main(String[] args) {
        PInjector injector = new PInjector(PineEngine.class.getPackageName());
        var windowService = (WindowService) injector.getBean(WindowService.class);
        windowService.openWindow(EditorWindow.class);

        File logFile = new File("engine.log");
        LogListener listener = new LogListener();

        Tailer tailer = Tailer.builder()
                .setFile(logFile)
                .setTailerListener(listener)
                .setDelayDuration(Duration.of(1, ChronoUnit.SECONDS))
                .get();
        new Thread(tailer).start();
    }
}
