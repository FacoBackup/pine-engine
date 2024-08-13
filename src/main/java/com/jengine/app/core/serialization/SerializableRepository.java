package com.jengine.app.core.serialization;

import java.util.List;

public interface SerializableRepository<T> {
    List<T> getAllData();

    void loadAllData(List<T> allData);
}
