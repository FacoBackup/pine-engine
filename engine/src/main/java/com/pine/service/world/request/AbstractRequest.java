package com.pine.service.world.request;

import com.pine.Loggable;
import com.pine.repository.WorldRepository;
import com.pine.service.world.WorldService;

public abstract class AbstractRequest implements Loggable {

    public abstract RequestMessage run(WorldRepository repository, WorldService service);
}
