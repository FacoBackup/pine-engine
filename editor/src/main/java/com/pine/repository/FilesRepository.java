package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.repository.streaming.StreamableResourceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PBean
public class FilesRepository implements SerializableRepository {
    public static final String ROOT_DIRECTORY_ID = "ROOT";

    public final Map<String, FSEntry> entry = new HashMap<>() {{
        var newD = new FSEntry("Files", ROOT_DIRECTORY_ID);
        put(ROOT_DIRECTORY_ID, newD);
    }};
    public final Map<String, List<String>> parentChildren = new HashMap<>(){{
        put(ROOT_DIRECTORY_ID, new ArrayList<>());
    }};
    public final Map<String, String> childParent = new HashMap<>();
    public final Map<StreamableResourceType, List<String>> byType = new HashMap<>(){{
        for(StreamableResourceType type : StreamableResourceType.values()) {
            put(type, new ArrayList<>());
        }
    }};
    public transient boolean isImporting;
}
