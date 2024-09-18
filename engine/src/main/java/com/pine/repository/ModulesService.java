package com.pine.repository;

import com.pine.Initializable;
import com.pine.injection.EngineDependency;
import com.pine.injection.EngineInjectable;
import com.pine.injection.EngineInjector;
import com.pine.injection.EngineExternalModule;
import com.pine.service.system.SystemService;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EngineInjectable
public class ModulesService {

    @EngineDependency
    public SystemService systemService;

    @EngineDependency
    public EngineInjector injector;

    private final Map<String, EngineExternalModule> modules = new HashMap<>();

    public Map<String, EngineExternalModule> getModules() {
        return modules;
    }

    public void addModules(List<EngineExternalModule> modules) {
        for (var m : modules) {
            this.modules.put(m.getClass().getName(), m);
            initializeModule(m, prepareModule(m));
        }

        for (var m : modules) {
            systemService.setSystems(m.getExternalSystems(new ArrayList<>(systemService.getSystems())));
        }
    }

    @NotNull
    private List<Object> prepareModule(EngineExternalModule m) {
        List<Object> injectables = m.getInjectables();
        injector.addInjectables(injectables);
        injector.addInjectables(List.of(m));
        injector.inject(m);
        injectables.forEach(i -> injector.inject(i));
        return injectables;
    }

    private static void initializeModule(EngineExternalModule m, List<Object> injectables) {
        m.onInitialize();
        injectables.forEach(e -> {
            if (e instanceof Initializable) {
                ((Initializable) e).onInitialize();
            }
        });
    }
}
