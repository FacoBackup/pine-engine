package com.pine.engine.core;

import com.pine.common.Initializable;
import com.pine.common.Loggable;
import com.pine.engine.Engine;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EngineInjector implements Loggable {
    private final List<Object> injectables = new ArrayList<>();

    /**
     * Scans classpath for EngineInjectable, injects dependencies and initializes them
     */
    public EngineInjector(Engine engine) {
        injectables.add(this);
        injectables.add(engine);

        collectInjectables();
        initializeInjectables();
    }

    private void initializeInjectables() {
        for (var in : injectables) {
            inject(in);
        }
        for (var in : injectables) {
            if (in instanceof Initializable) {
                ((Initializable) in).onInitialize();
            }
        }
        for (var in : injectables) {
            if (in instanceof LateInitializable) {
                ((LateInitializable) in).lateInitialize();
            }
        }
    }

    private void collectInjectables() {
        Reflections reflections = new Reflections(getClass().getPackageName(), Scanners.TypesAnnotated);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(EngineInjectable.class);
        for (Class<?> clazz : annotatedClasses) {
            try {
                injectables.add(clazz.getConstructor().newInstance());
            } catch (Exception e) {
                getLogger().error("Failed to instantiate {}", clazz.getSimpleName(), e);
            }
        }
    }

    /**
     * Injects dependency found on list of injectables
     *
     * @param instance: object to be injected with EngineDependency
     */
    public void inject(Object instance) {
        Field[] fields = instance.getClass().getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(EngineDependency.class)) {
                boolean isInjected = false;
                for (Object i : injectables) {
                    if (i.getClass() == field.getType()) {
                        isInjected = injectInternal(instance, field, i);
                        if (isInjected) {
                            break;
                        }
                    }
                }
                if (!isInjected) {
                    getLogger().error("{} dependency {} not injected", instance.getClass().getSimpleName(), field.getType().getSimpleName());
                }
            }
        }
    }

    private boolean injectInternal(Object instance, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
            return true;
        } catch (Exception e) {
            getLogger().error("Failed to inject dependency {} onto {}", field.getType().getSimpleName(), instance.getClass().getSimpleName());
            return false;
        }
    }

    public void addInjectables(List<Object> injectables) {
        this.injectables.addAll(injectables);
    }
}
