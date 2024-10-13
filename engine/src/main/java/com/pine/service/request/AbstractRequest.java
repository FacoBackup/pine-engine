package com.pine.service.request;

import com.pine.messaging.Loggable;
import com.pine.messaging.Message;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;

public abstract class AbstractRequest implements Loggable {

    public abstract Message run(WorldRepository repository, StreamingRepository streamingRepository);
}
