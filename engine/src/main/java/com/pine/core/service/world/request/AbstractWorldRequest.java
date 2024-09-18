package com.pine.core.service.world.request;

import com.pine.core.repository.WorldRepository;
import com.pine.core.service.world.WorldService;

public abstract class AbstractWorldRequest {

    public abstract RequestMessage run(WorldRepository repository, WorldService service);
}
