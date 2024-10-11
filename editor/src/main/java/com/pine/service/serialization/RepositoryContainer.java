package com.pine.service.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RepositoryContainer implements Serializable {
    public final List<Serializable> serializables = new ArrayList<>();
}
