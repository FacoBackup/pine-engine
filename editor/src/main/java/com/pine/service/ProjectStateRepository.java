package com.pine.service;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.injection.PInject;

import java.util.List;

@PBean
public class ProjectStateRepository implements SerializableRepository {
    @PInject
    public List<SerializableRepository> serializableRepositories;
}
