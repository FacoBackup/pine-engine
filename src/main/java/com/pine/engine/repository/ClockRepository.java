package com.pine.engine.repository;

import com.pine.common.injection.PBean;
import com.pine.engine.tasks.SyncTask;

@PBean
public class ClockRepository implements SyncTask {
    public long since = 0;
    public float deltaTime = 0;
    public long totalTime = 0;
    public transient long start = -1;

    @Override
    public void sync() {
        if(start == -1){
            start = System.currentTimeMillis();
        }
        totalTime = System.currentTimeMillis();
        deltaTime = (totalTime - since) / 1000f;
        since = totalTime;
    }
}