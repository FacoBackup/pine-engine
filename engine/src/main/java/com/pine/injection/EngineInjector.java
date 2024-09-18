package com.pine.injection;

import com.pine.Engine;
import com.pine.Initializable;
import com.pine.Loggable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EngineInjector implements Loggable, Initializable {
    private final List<Object> injectables = new ArrayList<>();
    private final Engine engine;


    public EngineInjector(Engine engine) {
        this.engine = engine;
    }

    /**
     * Scans classpath for EngineInjectable, injects dependencies and initializes them
     */
    @Override
    public void onInitialize() {
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
            if (in instanceof Initializable && in != this) {
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
        Reflections reflections = new Reflections(Engine.class.getPackageName(), Scanners.TypesAnnotated);
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
                try {
                    boolean isInjected;
                    Class<?> type = field.getType();
                    boolean isList = List.class.isAssignableFrom(type);
                    if (isList) {
                        isInjected = injectListField(instance, field);
                    } else {
                        isInjected = injectSimpleField(instance, field, type);
                    }

                    if (!isInjected) {
                        getLogger().error("{} dependency {} not injected", instance.getClass().getSimpleName(), type.getSimpleName());
                    }
                } catch (Exception e) {
                    getLogger().error("Could not inject dependency into {}", instance.getClass().getSimpleName(), e);
                }
            }
        }
    }

    private boolean injectListField(Object instance, Field field) throws ClassNotFoundException {
        List<Object> list = new ArrayList<>();
        Type genericType = field.getGenericType();
        Class<?> type;
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            type = Class.forName(typeArguments[0].getTypeName());
        } else {
            return false;
        }

        for (Object i : injectables) {
            if (type.isAssignableFrom(i.getClass())) {
                list.add(i);
            }
        }

        return injectInternal(instance, field, list);
    }

    private boolean injectSimpleField(Object instance, Field field, Class<?> type) {
        boolean isInjected = false;
        for (Object i : injectables) {
            if (i.getClass() == type) {
                isInjected = injectInternal(instance, field, i);
                if (isInjected) {
                    break;
                }
            }
        }
        return isInjected;
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
