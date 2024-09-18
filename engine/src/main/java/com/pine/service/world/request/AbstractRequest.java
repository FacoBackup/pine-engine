package com.pine.service.world.request;

import com.pine.repository.WorldRepository;
import com.pine.service.world.WorldService;

public abstract class AbstractRequest {

    public abstract RequestMessage run(WorldRepository repository, WorldService service);
}
