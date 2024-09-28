package com.pine.service;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.SerializableRepository;

import java.util.List;

@PBean
public class ProjectStateRepository implements SerializableRepository {
    @PInject
    public List<SerializableRepository> serializableRepositories;
}
