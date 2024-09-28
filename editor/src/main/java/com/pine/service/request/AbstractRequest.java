package com.pine.service.request;

import com.pine.Loggable;
import com.pine.repository.Message;
import com.pine.repository.WorldRepository;
import com.pine.service.world.WorldService;

public abstract class AbstractRequest implements Loggable {

    public abstract Message run(WorldRepository repository, WorldService service);
}
