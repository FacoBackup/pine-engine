package com.pine.service.request;

import com.pine.messaging.Loggable;
import com.pine.messaging.Message;
import com.pine.repository.WorldRepository;

public abstract class AbstractRequest implements Loggable {

    public abstract Message run(WorldRepository repository);

    public Object getResponse(){
        return null;
    }
}
