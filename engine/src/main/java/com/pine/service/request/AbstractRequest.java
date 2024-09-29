package com.pine.service.request;

import com.pine.Loggable;
import com.pine.Message;
import com.pine.repository.WorldRepository;

public abstract class AbstractRequest implements Loggable {

    public abstract Message run(WorldRepository repository);
}
