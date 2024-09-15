package com.pine.engine.core.service.world.request;

import com.pine.common.messages.Message;
import com.pine.engine.core.repository.WorldRepository;
import com.pine.engine.core.service.world.WorldService;

public abstract class AbstractWorldRequest {

    public abstract Message run(WorldRepository repository, WorldService service);
}
